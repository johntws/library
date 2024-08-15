package com.aeon.library.dto;

import com.aeon.library.entity.Borrower;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class BorrowBookRes {
    private Long id;
    private BookDto book;
    private BorrowerDto borrower;
    private Timestamp issueDate;
    private Timestamp dueDate;
}
