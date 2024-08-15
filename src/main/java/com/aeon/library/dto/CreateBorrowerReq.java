package com.aeon.library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBorrowerReq {
    @NotBlank
    private String name;

    @Email(message = "Email is not valid")
    private String email;
}
