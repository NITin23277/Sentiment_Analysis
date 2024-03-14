package com.authentication.aimodel.controllers;

import com.authentication.aimodel.pojo.FbResponse;
import com.authentication.aimodel.services.FacebookService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/java/App")
public class FacebookController {

    @Autowired
    private FacebookService facebookService;

    @GetMapping("/latestComments")
    public ResponseEntity<List<FbResponse>> getLatestComments(@RequestParam("pageId") String pageId, @RequestParam("count") int count) {
        return facebookService.getLatestCommentsFromPosts(pageId, count);
    }
}
