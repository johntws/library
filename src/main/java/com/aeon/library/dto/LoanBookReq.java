package com.aeon.library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoanBookReq {
    @NotNull
    private Long copyId;

    @Email
    private String email;

    @NotEmpty
    private String dueDate;
}
