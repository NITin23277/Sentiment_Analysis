package com.authentication.aimodel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.authentication.aimodel.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);

}
