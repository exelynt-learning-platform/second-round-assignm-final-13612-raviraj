package com.webproj.ecom.proj.controller;

import com.webproj.ecom.proj.model.ApiResponse;
import com.webproj.ecom.proj.model.Product;
import com.webproj.ecom.proj.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // 1️⃣ Add a new product (only accessible by ADMIN)
    @PostMapping("/admin/add")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody Product product) throws IOException {
        if (product.getName() == null || product.getName().isBlank() ||
                product.getDescription() == null || product.getDescription().isBlank() ||
                product.getPrice() <= 0 ||
                product.getStockQuantity() < 0 ||
                product.getImgUrl() == null || product.getImgUrl().isBlank()) {

            return new ResponseEntity<>(new ApiResponse("All fields are required and must be valid"), HttpStatus.BAD_REQUEST);
        }

        productService.addProd(product);
        return new ResponseEntity<>(new ApiResponse("Product created successfully"), HttpStatus.CREATED);
    }

    // 2️⃣ Get product by ID (accessible by anyone)
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProdById(id);
        return ResponseEntity.ok(product);
    }

    // 3️⃣ Update a product (only accessible by ADMIN)
    @PutMapping("/admin/update")
    public ResponseEntity<ApiResponse> updateProduct(@RequestBody Product product) throws IOException {
        if (product.getId() == null ||
                product.getName() == null || product.getName().isBlank() ||
                product.getDescription() == null || product.getDescription().isBlank() ||
                product.getPrice() <= 0 ||
                product.getStockQuantity() < 0 ||
                product.getImgUrl() == null || product.getImgUrl().isBlank()) {

            return new ResponseEntity<>(new ApiResponse("All fields are required and must be valid"), HttpStatus.BAD_REQUEST);
        }

        Product updatedProduct = productService.updateProduct(product);
        return ResponseEntity.ok(new ApiResponse("Product updated successfully"));
    }

    // 4️⃣ Delete a product (only accessible by ADMIN)
    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(new ApiResponse("Product deleted successfully"));
    }

    // 5️⃣ Get all products (accessible by anyone)
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
}
