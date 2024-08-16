package com.aeon.library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReturnBookReq {
    @NotNull
    private Long copyId;

    @Email
    private String email;
}
