package com.webproj.ecom.proj.service;

import com.webproj.ecom.proj.model.*;
import com.webproj.ecom.proj.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private com.webproj.ecom.proj.service.AuthUtil authUtil;

    @Transactional
    public void processOrder(User user) {




        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus("SUCCESS");
        order.setCreatedAt(new Date());

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (CartItem item : cart.getCartItems()) {

            Product product = item.getProduct();

            int newStock = product.getStockQuantity() - item.getQuantity();

            if (newStock < 0) {
                throw new RuntimeException("Out of stock");
            }

            product.setStockQuantity(newStock);
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setOrder(order);

            orderItems.add(orderItem);

            total += product.getPrice() * item.getQuantity();
        }

        order.setItems(orderItems);
        order.setTotalAmount(total);

        orderRepository.save(order);

        // clear cart
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }
}