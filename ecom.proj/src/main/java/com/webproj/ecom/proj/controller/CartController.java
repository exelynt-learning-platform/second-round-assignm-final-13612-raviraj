package com.webproj.ecom.proj.controller;

import com.webproj.ecom.proj.handler.ResourceNotFoundException;
import com.webproj.ecom.proj.handler.UnauthorizedException;
import com.webproj.ecom.proj.model.*;
import com.webproj.ecom.proj.service.AuthUtil;
import com.webproj.ecom.proj.service.CartService;
import com.webproj.ecom.proj.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private AuthUtil authUtil;

    // 1️⃣ Add product to cart
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProductToCart(@RequestBody AddToCartRequest request) throws IOException {
        User user = authUtil.getLoggedInUser();
        if (user == null) {
            return new ResponseEntity<>(new ApiResponse("User not logged in"), HttpStatus.UNAUTHORIZED);
        }

        Product product = productService.getProdById(request.getProductId());

        Cart cart;
        try {
            cart = cartService.getCartByUser(user);
        } catch (ResourceNotFoundException e) {
            cart = new Cart();
            cart.setUser(user);
            cart.setCartItems(new ArrayList<>());
        }

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(ci -> ci.getProduct().getId() == product.getId())
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + request.getQuantity());
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(cart);
            cartItem.setQuantity(request.getQuantity());
            cart.getCartItems().add(cartItem);
        }

        cartService.addCart(cart);
        return new ResponseEntity<>(new ApiResponse("Product added to cart successfully"), HttpStatus.CREATED);
    }

    // 2️⃣ Get current user's cart
    @GetMapping("/showCart")
    public ResponseEntity<List<CartItem>> getUserCart() {
        User user = authUtil.getLoggedInUser();
        if (user == null) {
            throw new UnauthorizedException("User not logged in");
        }
        Cart cart = cartService.getCartByUser(user);
        return ResponseEntity.ok(cart.getCartItems());
    }

    // 3️⃣ Remove a product from cart
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<ApiResponse> removeProductFromCart(@PathVariable Long productId) {
        User user = authUtil.getLoggedInUser();
        Cart cart = cartService.getCartByUser(user);

        CartItem itemToRemove = cart.getCartItems().stream()
                .filter(ci -> ci.getProduct().getId() == productId)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart"));

        cart.getCartItems().remove(itemToRemove);
        cartService.addCart(cart);

        return ResponseEntity.ok(new ApiResponse("Product removed from cart"));
    }

    // 4️⃣ Update quantity of a product
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateProductQuantity(@RequestBody AddToCartRequest request) {
        User user = authUtil.getLoggedInUser();
        Cart cart = cartService.getCartByUser(user);

        CartItem item = cart.getCartItems().stream()
                .filter(ci -> ci.getProduct().getId() == request.getProductId())
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart"));

        item.setQuantity(request.getQuantity());
        cartService.addCart(cart);

        return ResponseEntity.ok(new ApiResponse("Quantity updated successfully"));
    }

    // 5️⃣ Clear user's cart
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse> clearCart() {
        User user = authUtil.getLoggedInUser();
        Cart cart = cartService.getCartByUser(user);

        cart.getCartItems().clear();
        cartService.addCart(cart);

        return ResponseEntity.ok(new ApiResponse("Cart cleared successfully"));
    }

}
