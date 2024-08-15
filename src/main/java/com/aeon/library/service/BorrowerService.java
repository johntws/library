package com.aeon.library.service;


import com.aeon.library.dto.BorrowBookReq;
import com.aeon.library.dto.BorrowBookRes;
import com.aeon.library.dto.CreateBorrowerReq;
import com.aeon.library.dto.CreateBorrowerRes;
import com.aeon.library.exception.GeneralException;

public interface BorrowerService {
    BorrowBookRes borrowBook(Long borrowerId, BorrowBookReq request) throws GeneralException;
    BorrowBookRes returnBook(Long borrowerId, BorrowBookReq request) throws GeneralException;
    CreateBorrowerRes registerBorrower(CreateBorrowerReq createBorrowerReq) throws GeneralException;
}
