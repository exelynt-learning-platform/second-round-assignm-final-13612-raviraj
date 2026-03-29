package com.webproj.ecom.proj.controller;

import com.webproj.ecom.proj.repo.UserRepository;
import com.webproj.ecom.proj.service.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Links;
import com.paypal.base.rest.PayPalRESTException;

import com.webproj.ecom.proj.service.PaypalService;
import com.webproj.ecom.proj.service.OrderService;

import com.webproj.ecom.proj.repo.CartRepository;

import com.webproj.ecom.proj.model.Cart;
import com.webproj.ecom.proj.model.CartItem;
import com.webproj.ecom.proj.model.User;

import jakarta.transaction.Transactional;
@RestController
@RequestMapping("/api/paypal")
public class PaypalController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaypalService paypalService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private OrderService orderService;

    @GetMapping("/pay")
    public String pay() throws PayPalRESTException {
        User user = authUtil.getLoggedInUser();



        if (user == null) {
            return "User not logged in ❌";
        }

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        double total = 0;

        for (CartItem item : cart.getCartItems()) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }

        String email = user.getEmail();

        Payment payment = paypalService.createPayment(total, email);

        for (Links link : payment.getLinks()) {
            if ("approval_url".equals(link.getRel())) {
                return link.getHref();
            }
        }

        return "Error creating payment";
    }

    @GetMapping("/success")
    @Transactional
    public String success(@RequestParam String paymentId,
                          @RequestParam String PayerID,
                          @RequestParam String email) throws PayPalRESTException {

        Payment payment = paypalService.executePayment(paymentId, PayerID);

        if (!"approved".equals(payment.getState())) {
            return "Payment failed ❌";
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

                 orderService.processOrder(user);
                 return "Order placed successfully ✅";
    }

    @GetMapping("/cancel")
    public String cancel() {
        return "Payment Cancelled ❌";
    }
}