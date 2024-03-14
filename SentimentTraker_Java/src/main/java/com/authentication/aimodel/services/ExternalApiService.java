package com.authentication.aimodel.services;

import com.authentication.aimodel.entity.User;
import com.authentication.aimodel.pojo.AnalysisResponse;
import com.authentication.aimodel.pojo.UserCredentials;
import com.authentication.aimodel.repositories.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


@Service
public class ExternalApiService {

    @Autowired
    private UserRepository userRepository;

    // Assuming you have a UserRepository to fetch user emails
    private static final Logger logger = LogManager.getLogger(ExternalApiService.class);

//    @Value("${spring.apiUrl}")
    String apiUrl = "http://localhost:5000//analyzed-comments";

    // Method to hit the external API and process the response
    public ResponseEntity<AnalysisResponse> hitExternalApiAndNotifyUsers(String username) {
       
        AnalysisResponse analysisResponse = new AnalysisResponse();
        // Create HttpClient instance
        HttpClient httpClient = HttpClients.createDefault();

        // Create HttpGet request
        HttpGet request = new HttpGet(apiUrl);

        try {
            // Execute the request
            HttpResponse response = httpClient.execute(request);

            // Process the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }

            // Parse the JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody.toString());
            JsonNode commentsNode = rootNode.get("comments");

            List<List<String>> storeNegative = new ArrayList<>(); 
            List<String> negatives = null;
            // Process each comment
            for (JsonNode commentNode : commentsNode) {
                JsonNode sentimentNode = commentNode.get("sentiment");
                if (sentimentNode != null) {
                    String label = sentimentNode.get("label").asText();
                    if ("negative".equalsIgnoreCase(label)) {
                        negatives = new ArrayList<>();
                        String from = commentNode.get("from").asText();
                        String message = commentNode.get("message").asText();
                        negatives.add(from);
                        negatives.add(message);
                        // Get the user's email based on username
                        storeNegative.add(negatives);
                    }    
                }
            }

//            User user = userRepository.findByUsername(username);
//            logger.info("userEmail -> "+ user.getEmail());
            String email = "theprimeblogger@gmail.com";
            sendEmailNotification(email, storeNegative);
            analysisResponse.setStatus("success");
            analysisResponse.setRespCode("200");
            analysisResponse.setMessage("Email Sent Successfully");
            

        } catch (IOException e) {
            e.printStackTrace(); 
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(analysisResponse, HttpStatus.OK);
    }

   

    // Method to send email notification to the user
    private void sendEmailNotification(String userEmail, List<List<String>> storeNegative) {
        // Email configuration
        String username = userEmail; // Your email address
        String password = "qdwc obmq rgko nxpw"; // Your email password
        String host = "smtp.gmail.com"; // Your SMTP server host
        int port = 587; // Your SMTP server port (e.g., 587 for TLS)

        // Email properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        // Create a session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a MimeMessage
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(username)); // Set sender email
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(userEmail)); // Set recipient email
            mimeMessage.setSubject("Negative Comment Notification"); // Set email subject

            // Build email body
            StringBuilder emailBody = new StringBuilder();
            emailBody.append("Dear User,\n\n");
            emailBody.append("Negative comments were received:\n\n");

            System.out.println("Number of negative comments: " + storeNegative.size());
            for (List<String> negativeComment : storeNegative) {
                System.out.println("Comment: " + negativeComment);
                // Your existing logic
            }


            for (List<String> negativeComment : storeNegative) {
                if (negativeComment.size() >= 2) { // Check if there are at least two elements
                    String from = negativeComment.get(0);    // 'from' is the first element
                    String message = negativeComment.get(1); // 'message' is the second element

                    // Append negative comment details to email body
                    emailBody.append("From: ").append(from).append("\n");
                    emailBody.append("Message: ").append(message).append("\n\n");
                }
            }

            emailBody.append("Please take necessary action.\n\n");
            emailBody.append("Regards,\nYour Application");

            mimeMessage.setText(emailBody.toString()); // Set email body

            // Send the email
            Transport.send(mimeMessage);

            System.out.println("Email sent successfully to " + userEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    
}
