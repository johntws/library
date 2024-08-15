package com.aeon.library.controller;

import com.aeon.library.dto.BorrowBookReq;
import com.aeon.library.dto.BorrowBookRes;
import com.aeon.library.dto.CreateBorrowerReq;
import com.aeon.library.dto.CreateBorrowerRes;
import com.aeon.library.exception.GeneralException;
import com.aeon.library.service.BorrowerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/borrowers")
public class BorrowerController {

    private final BorrowerService borrowerService;

    public BorrowerController(BorrowerService borrowerService) {
        this.borrowerService = borrowerService;
    }

    @PostMapping
    public CreateBorrowerRes registerBorrower(@Valid @RequestBody CreateBorrowerReq request) throws GeneralException {
        return borrowerService.registerBorrower(request);
    }

    @PostMapping("/{borrowerId}/borrow")
    public BorrowBookRes borrowBook(@PathVariable @Valid @NotNull Long borrowerId, @Valid @RequestBody BorrowBookReq request) throws GeneralException {
        return borrowerService.borrowBook(borrowerId, request);
    }

    @PostMapping("/{borrowerId}/return")
    public BorrowBookRes returnBook(@PathVariable @Valid @NotNull Long borrowerId, @RequestBody BorrowBookReq request) throws GeneralException {
        return borrowerService.returnBook(borrowerId, request);
    }

}
