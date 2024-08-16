package com.aeon.library.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class ReturnBookRes {
    private Long loanId;
    private BookDto book;
    private MemberDto member;
    private Date issueDate;
    private Date dueDate;
    private Date returnDate;
}
