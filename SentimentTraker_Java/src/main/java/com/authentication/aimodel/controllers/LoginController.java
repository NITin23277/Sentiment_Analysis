package com.authentication.aimodel.controllers;

import com.authentication.aimodel.pojo.LoginResponse;
import com.authentication.aimodel.pojo.UserCredentials;
import com.authentication.aimodel.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/java/App")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserCredentials credentials) {
        LoginResponse response = new LoginResponse();
        if (loginService.authenticateUser(credentials)) {
            response.setStatus("success");
            response.setRespCode("200");
            return new ResponseEntity<>(response,HttpStatus.OK);
        } else {
            response.setStatus("failure");
            response.setRespCode("400");
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
    }
}
