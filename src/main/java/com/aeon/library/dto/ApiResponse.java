package com.aeon.library.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private String responseCode;
    private String message;
    // error list
    private T data;

    public ApiResponse(String responseCode, String message, T data) {
        this.responseCode = responseCode;
        this.message = message;
        this.data = data;
    }
}
