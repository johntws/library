package com.aeon.library.dto;

import com.aeon.library.entity.Borrower;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class BorrowBookRes {
    private Long id;
    private BookDto book;
    private Borrower borrower;
    private Timestamp borrowedDate;
    private Timestamp returnDate;
}
