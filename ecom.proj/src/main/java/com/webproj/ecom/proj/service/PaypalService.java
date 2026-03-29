package com.webproj.ecom.proj.service;

import com.paypal.api.payments.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import java.util.ArrayList;
import java.util.List;

    @Service
    public class PaypalService {

        @Autowired
        private APIContext apiContext;

        public Payment createPayment(Double total, String email) throws PayPalRESTException {

            Amount amount = new Amount();
            amount.setCurrency("USD"); // PayPal uses USD mostly
            amount.setTotal(String.format("%.2f", total));

            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setDescription("E-commerce payment");

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);

            Payer payer = new Payer();
            payer.setPaymentMethod("paypal");

            Payment payment = new Payment();
            payment.setIntent("sale");
            payment.setPayer(payer);
            payment.setTransactions(transactions);

            RedirectUrls urls = new RedirectUrls();
            urls.setCancelUrl("http://localhost:8081/api/paypal/cancel");  // ✅ ADD
            urls.setReturnUrl("http://localhost:8081/api/paypal/success?email=" + email);
            payment.setRedirectUrls(urls);

            return payment.create(apiContext);
        }
        public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {

            Payment payment = new Payment();
            payment.setId(paymentId);

            PaymentExecution execution = new PaymentExecution();
            execution.setPayerId(payerId);

            return payment.execute(apiContext, execution);
        }
    }

