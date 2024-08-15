package com.aeon.library.controller;

import com.aeon.library.dto.*;
import com.aeon.library.exception.GeneralException;
import com.aeon.library.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public CreateMemberRes registerBorrower(@Valid @RequestBody CreateMemberReq request) throws GeneralException {
        return memberService.registerBorrower(request);
    }

    @PostMapping("/{id}/loan")
    public BorrowBookRes borrowBook(@PathVariable @Valid @NotNull Long id,
                                    @Valid @RequestBody BorrowBookReq request) throws GeneralException {
        return memberService.borrowBook(id, request);
    }

    @PostMapping("/{id}/return")
    public ReturnBookRes returnBook(@PathVariable @Valid @NotNull Long id,
                                    @Valid @RequestBody ReturnBookReq request) throws GeneralException {
        return memberService.returnBook(id, request);
    }

}
