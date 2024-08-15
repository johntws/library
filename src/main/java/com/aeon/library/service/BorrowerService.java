package com.aeon.library.service;


import com.aeon.library.dto.*;
import com.aeon.library.exception.GeneralException;

public interface BorrowerService {
    BorrowBookRes borrowBook(Long borrowerId, BorrowBookReq request) throws GeneralException;
    ReturnBookRes returnBook(Long borrowerId, ReturnBookReq request) throws GeneralException;
    CreateMemberRes registerBorrower(CreateMemberReq createMemberReq) throws GeneralException;
}
