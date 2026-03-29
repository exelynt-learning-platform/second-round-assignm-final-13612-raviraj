package com.webproj.ecom.proj.service;

import com.webproj.ecom.proj.config.AppConfig;
import com.webproj.ecom.proj.handler.BadRequestException;
import com.webproj.ecom.proj.handler.ConflictException;
import com.webproj.ecom.proj.handler.ResourceNotFoundException;
import com.webproj.ecom.proj.model.ApiResponse;
import com.webproj.ecom.proj.model.Cart;
import com.webproj.ecom.proj.model.User;
import com.webproj.ecom.proj.repo.CartRepository;
import com.webproj.ecom.proj.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Create user
    public void createUser(User user)  {

        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            throw new ConflictException("User already exists");
        }

        if (!user.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d).{6,}$")) {
            throw new BadRequestException("Use Strong Password");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
    }

    // Get user by email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    // Delete user by email
    public void deleteUserByEmail(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (!existingUser.isPresent()) {
            throw new ResourceNotFoundException("User with this email does not exist");
        }
        userRepository.deleteByEmail(email);
    }

    // Update user
    public void updateUser(User user) throws IOException {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (!existingUser.isPresent()) {
            throw new ResourceNotFoundException("User with this email does not exist");
        }

        User updatedUser = existingUser.get();
        updatedUser.setName(user.getName());
        updatedUser.setPassword(user.getPassword());
        userRepository.save(updatedUser);
    }
}
