package com.webproj.ecom.proj.service;

import com.webproj.ecom.proj.handler.ResourceNotFoundException;
import com.webproj.ecom.proj.model.ApiResponse;
import com.webproj.ecom.proj.model.Product;
import com.webproj.ecom.proj.repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Add a new product
    public Product addProd(Product product) throws IOException {
        return productRepository.save(product);
    }

    // Get product by ID
    public Product getProdById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found "));
    }

    // Delete a product
    public void delete(Long id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found "));
        productRepository.delete(existingProduct);
    }

    // Update a product
    public Product updateProduct(Product product) throws IOException {
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found "));
        return productRepository.save(product);
    }

    // Get all products
    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found");
        }
        return products;
    }
}
