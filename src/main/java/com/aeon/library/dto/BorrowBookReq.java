package com.aeon.library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BorrowBookReq {
    @NotNull
    private Long id;

    @Email
    private String email;
}
