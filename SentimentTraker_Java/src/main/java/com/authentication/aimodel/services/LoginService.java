package com.authentication.aimodel.services;

import com.authentication.aimodel.entity.User;
import com.authentication.aimodel.pojo.UserCredentials;
import com.authentication.aimodel.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    public boolean authenticateUser(UserCredentials credentials) {
        // Retrieve user from database by username
        User user = userRepository.findByUsername(credentials.getUser());

        return user != null && user.getPassword().equals(credentials.getPass());
    }
}
