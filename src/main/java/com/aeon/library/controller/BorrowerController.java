package com.aeon.library.controller;

import com.aeon.library.dto.*;
import com.aeon.library.exception.GeneralException;
import com.aeon.library.service.BorrowerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/borrowers")
public class BorrowerController {

    private final BorrowerService borrowerService;

    public BorrowerController(BorrowerService borrowerService) {
        this.borrowerService = borrowerService;
    }

    @PostMapping
    public CreateMemberRes registerBorrower(@Valid @RequestBody CreateMemberReq request) throws GeneralException {
        return borrowerService.registerBorrower(request);
    }

    @PostMapping("/{borrowerId}/borrow")
    public BorrowBookRes borrowBook(@PathVariable @Valid @NotNull Long borrowerId,
                                    @Valid @RequestBody BorrowBookReq request) throws GeneralException {
        return borrowerService.borrowBook(borrowerId, request);
    }

    @PostMapping("/{borrowerId}/return")
    public ReturnBookRes returnBook(@PathVariable @Valid @NotNull Long borrowerId,
                                    @Valid @RequestBody ReturnBookReq request) throws GeneralException {
        return borrowerService.returnBook(borrowerId, request);
    }

}
