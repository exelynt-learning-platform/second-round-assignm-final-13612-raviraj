package com.webproj.ecom.proj.service;

import com.webproj.ecom.proj.handler.ResourceNotFoundException;
import com.webproj.ecom.proj.model.ApiResponse;
import com.webproj.ecom.proj.model.Cart;
import com.webproj.ecom.proj.model.Product;
import com.webproj.ecom.proj.model.User;
import com.webproj.ecom.proj.repo.CartRepository;
import com.webproj.ecom.proj.repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    // Add or create a cart
    public Cart addCart(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }
        return cartRepository.save(cart);
    }

    // Get cart by user
    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));
    }

    // Update cart
    public Cart updateCart(Cart cart) throws IOException {
        Optional<Cart> existingCart = cartRepository.findById(cart.getId());
        if (!existingCart.isPresent()) {
            throw new ResourceNotFoundException("Cart not found with id: " + cart.getId());
        }
        return cartRepository.save(cart);
    }

    // Delete cart by ID
    public void deleteCart(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + id));
        cartRepository.delete(cart);
    }

    // Get all carts
    public List<Cart> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if (carts.isEmpty()) {
            throw new ResourceNotFoundException("No carts found");
        }
        return carts;
    }
}