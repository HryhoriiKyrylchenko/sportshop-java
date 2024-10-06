package com.sportshop.sportshop.service;

import com.sportshop.sportshop.model.User;
import com.sportshop.sportshop.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(User user) {
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        existingUser.setFullName(user.getFullName());
        existingUser.setCity(user.getCity());
        existingUser.setCountry(user.getCountry());
        existingUser.setPhone(user.getPhone());
        existingUser.setEmail(user.getEmail());

        userRepository.save(existingUser);
    }


    public boolean isValidUser(String username, String password) {
        User user = userRepository.findByEmail(username);
        if (user != null) {

            return Objects.equals(password, user.getPassword());
        }
        return false;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findAllNonAdmins() {
        return userRepository.findByIsAdminFalse();
    }

    public void blockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        user.setBlocked(true);
        userRepository.save(user);
    }

    public void unblockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        user.setBlocked(false);
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new IllegalArgumentException("User not found: " + id);
        }
    }
}
