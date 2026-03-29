package com.webproj.ecom.proj.config;

import com.webproj.ecom.proj.components.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Autowired
    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

// 🔥 Skip JWT for auth APIs


        String header = request.getHeader("Authorization");

        // 1️⃣ If Authorization header exists and starts with "Bearer"
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7); // Extract token

            try {
                // 2️⃣ Extract username and role from JWT
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token); // The role already has the 'ROLE_' prefix

                // 3️⃣ Set roles/authorities
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(role)); // The role is already prefixed with 'ROLE_'

                // 4️⃣ Create an Authentication token with the username and roles
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                // 5️⃣ Set authentication in the security context
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                System.out.println("Invalid JWT: " + e.getMessage());
            }
        }

        // 6️⃣ Continue the filter chain (pass the request along)
        filterChain.doFilter(request, response);
    }
}