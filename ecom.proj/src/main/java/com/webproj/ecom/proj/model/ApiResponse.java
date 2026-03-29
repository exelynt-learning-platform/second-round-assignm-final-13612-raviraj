package com.webproj.ecom.proj.model;


import lombok.Data;
import org.springframework.web.bind.annotation.ResponseStatus;
@Data
public class ApiResponse {

    private String message;

    public ApiResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String value() {
        return message;
    }
}