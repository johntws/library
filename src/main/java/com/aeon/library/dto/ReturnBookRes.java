package com.aeon.library.dto;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

@Data
public class ReturnBookRes {
    private Long id;
    private BookDto book;
    private BorrowerDto borrower;
    private Date issueDate;
    private Date dueDate;
    private Date returnDate;
}
