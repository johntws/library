package com.aeon.library.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ReturnBookRes {
    private Long id;
    private BookDto book;
    private BorrowerDto borrower;
    private Timestamp issueDate;
    private Timestamp dueDate;
    private Timestamp returnDate;
}
