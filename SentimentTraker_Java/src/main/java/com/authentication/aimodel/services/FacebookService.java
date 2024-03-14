package com.authentication.aimodel.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.authentication.aimodel.pojo.FbResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Service
public class FacebookService {

    private static final Logger logger = LogManager.getLogger(FacebookService.class);

    @Value("${fb.access-token}")
    private String accessToken;

    @Value("${fb.url}")
    private String url;

    public ResponseEntity<List<FbResponse>> getLatestCommentsFromPosts(String pageId, int count) {
        logger.info("Fetching latest comments from Facebook posts...");

        List<FbResponse> comments = new ArrayList<>();

        // Construct the URL to fetch latest posts
        String postsUrl = url + pageId + "/feed?fields=id,comments.limit(" + count + ")&access_token=" + accessToken;
        logger.info("url ->" + postsUrl);
        // Make HTTP GET request to Facebook Graph API
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(postsUrl, String.class);

        try {
            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray data = jsonResponse.getJSONArray("data");
            System.out.println(data.length());
            logger.info("data ->" + data.toString());

            // Iterate through each post
            for (int i = 0; i < data.length(); i++) {
                JSONObject post = data.getJSONObject(i);

                // Check if the post has comments
                if (post.has("comments")) {
                    JSONObject commentsObject = post.getJSONObject("comments");
                    JSONArray commentsArray = commentsObject.getJSONArray("data");
                    logger.info("commentsArray ->" + commentsArray.toString());
                    // Iterate through comments of the post
                    for (int j = 0; j < commentsArray.length(); j++) {
                        JSONObject comment = commentsArray.getJSONObject(j);
                        FbResponse fbComment = new FbResponse();
                        fbComment.setFrom(comment.getJSONObject("from").getString("name"));
                        fbComment.setId(comment.getJSONObject("from").getString("id"));
                        fbComment.setMessage(comment.getString("message"));
                        comments.add(fbComment);
                    }
                }
                // Process and sort comments by creation time, handling potential null values
                comments.sort(Comparator.comparing(FbResponse::getCreatedTime, Comparator.nullsLast(Comparator.naturalOrder())).reversed());

                // Limit the number of comments to the specified count
                if (comments.size() > count) {
                    comments = comments.subList(0, count);
                }
                logger.info("final comments array ->" + comments.toString());
            }
        } catch (JSONException e) {
            logger.error("Error parsing JSON response", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("Finished fetching latest comments from Facebook posts.");
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
}
