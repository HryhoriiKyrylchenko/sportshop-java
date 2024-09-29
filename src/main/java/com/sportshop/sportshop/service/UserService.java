package com.sportshop.sportshop.service;

import com.sportshop.sportshop.model.User;
import com.sportshop.sportshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public boolean isValidUser(String username, String password) {
        User user = userRepository.findByEmail(username);
        if (user != null) {

            return Objects.equals(password, user.getPassword());
        }
        return false;
    }
}
