package com.aeon.library.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBookRes {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private boolean borrowed;
}
