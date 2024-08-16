package com.aeon.library.dto;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

@Data
public class BorrowBookRes {
    private Long id;
    private BookDto book;
    private BorrowerDto borrower;
    private Date issueDate;
    private Date dueDate;
}
