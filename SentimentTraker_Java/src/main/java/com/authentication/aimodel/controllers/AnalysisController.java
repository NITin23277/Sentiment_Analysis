package com.authentication.aimodel.controllers;

import com.authentication.aimodel.pojo.AnalysisResponse;
import com.authentication.aimodel.services.ExternalApiService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/java/App")
public class AnalysisController {

     ExternalApiService externalApiService = new ExternalApiService();

    // Endpoint to hit external API and notify users
    @GetMapping("/notifyUsers")
    public ResponseEntity<AnalysisResponse> notifyUsers(@RequestParam("username") String username) {
       
       return externalApiService.hitExternalApiAndNotifyUsers(username);
    
    }
}
