package com.webproj.ecom.proj.repo;

import com.webproj.ecom.proj.model.ApiResponse;
import com.webproj.ecom.proj.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;



import com.webproj.ecom.proj.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username); // ✅ needed for AuthUtil

    void deleteByEmail(String email);
}