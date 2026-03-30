package com.webproj.ecom.proj.controller;

import com.webproj.ecom.proj.components.JwtUtil;
import com.webproj.ecom.proj.handler.BadRequestException;
import com.webproj.ecom.proj.handler.ConflictException;
import com.webproj.ecom.proj.handler.ResourceNotFoundException;
import com.webproj.ecom.proj.handler.UnauthorizedException;
import com.webproj.ecom.proj.model.ApiResponse;
import com.webproj.ecom.proj.model.LoginRequest;
import com.webproj.ecom.proj.model.RegisterRequest;
import com.webproj.ecom.proj.model.User;
import com.webproj.ecom.proj.service.UserService;
import org.hibernate.metamodel.internal.AbstractPojoInstantiator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseExtractor;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // 🔑 Login API
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {

        User user = userService.getUserByEmail(request.getUsername());

        // 1️⃣ Check if user exists
        if (user == null) {
            throw new ResourceNotFoundException("User Not Found");
        }

        // 2️⃣ Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Wrong Email Or Password");
        }

        // 3️⃣ Add role to JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());

        String token = jwtUtil.generateToken(user.getEmail(), claims);

        // 4️⃣ Response
        ApiResponse response = new ApiResponse("Login successful");
        response.setMessage(token);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {

        // 1️⃣ Validate all required fields
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BadRequestException("Name is required");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }

        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new BadRequestException("Invalid email format");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Password is required");
        }

        if (!request.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d).{6,}$")) {
            throw new BadRequestException("Password must be at least 6 characters long and contain letters and numbers");
        }

        // 2️⃣ Check if user already exists
        User existingUser = userService.getUserByEmail(request.getEmail());
        if (existingUser != null) {
            throw new ConflictException("Email already registered");
        }

        // 3️⃣ Create new user
        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim());

        // Hash password before saving
        user.setPassword(request.getPassword()); // Hashing the password
        user.setRole(request.getRole() != null ? request.getRole() : "ROLE_USER"); // default role for new users

        userService.createUser(user);

        // 4️⃣ Return success response
        ApiResponse response = new ApiResponse("User registered successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
