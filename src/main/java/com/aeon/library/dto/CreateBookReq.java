package com.aeon.library.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBookReq {
    @NotBlank
    private String isbn;

    @NotBlank
    private String title;

    @NotBlank
    private String author;
}
