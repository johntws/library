package com.aeon.library.service.impl;

import com.aeon.library.dto.*;
import com.aeon.library.entity.Copy;
import com.aeon.library.entity.Member;
import com.aeon.library.entity.Loan;
import com.aeon.library.exception.GeneralException;
import com.aeon.library.repo.CopyRepository;
import com.aeon.library.repo.MemberRepository;
import com.aeon.library.repo.LoanRepository;
import com.aeon.library.service.MemberService;
import com.aeon.library.util.DateUtil;
import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.sql.Date;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {
    private final CopyRepository copyRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;
    private final DozerBeanMapper mapper;

    public MemberServiceImpl(MemberRepository memberRepository,
                             CopyRepository copyRepository, LoanRepository loanRepository,
                             DozerBeanMapper dozerBeanMapper) {
        this.memberRepository = memberRepository;
        this.copyRepository = copyRepository;
        this.loanRepository = loanRepository;
        this.mapper = dozerBeanMapper;
    }

    @Override
    public BorrowBookRes borrowBook(Long borrowerId, BorrowBookReq request) throws GeneralException {
        Date today = new Date(Instant.now().toEpochMilli());
        Date dueDate = DateUtil.stringToDate(request.getDueDate(), DateUtil.YYYY_MM_DD);
        if (dueDate.after(today) == false) {
            throw new GeneralException("Due date is before today");
        }

        Optional<Member> memberOpt = memberRepository.findByEmail(request.getEmail());
        if (memberOpt.isPresent() == false) {
            throw new GeneralException("Email doesn't exist");
        }

        Optional<Copy> copyOpt = copyRepository.findByIdAndDeletedFalse(request.getId());
        if (copyOpt.isPresent() == false) {
            throw new GeneralException("Book doesn't exist");
        }

        Copy copy = copyOpt.get();
        if (copy.isBorrowed()) {
            throw new GeneralException("Book is currently borrowed");
        }

        Loan loan = new Loan();
        loan.setMember(memberOpt.get());
        loan.setCopy(copy);
        loan.setIssueDate(today);
        loan.setDueDate(dueDate);
        loan.getCopy().setBorrowed(true);

        loanRepository.save(loan);

        BorrowBookRes borrowBookRes = new BorrowBookRes();

        BookDto bookDto = new BookDto();
        bookDto.setIsbn(copy.getBook().getIsbn());
        bookDto.setAuthor(copy.getBook().getAuthor());
        bookDto.setTitle(copy.getBook().getTitle());
        bookDto.setId(copy.getId());

        BorrowerDto borrowerDto = new BorrowerDto();
        borrowerDto.setEmail(memberOpt.get().getEmail());
        borrowerDto.setName(memberOpt.get().getName());
        borrowerDto.setId(memberOpt.get().getId());

        borrowBookRes.setBook(bookDto);
        borrowBookRes.setIssueDate(loan.getIssueDate());
        borrowBookRes.setDueDate(loan.getDueDate());
        borrowBookRes.setId(loan.getId());
        borrowBookRes.setBorrower(borrowerDto);

        return borrowBookRes;
    }

    @Override
    public ReturnBookRes returnBook(Long borrowerId, ReturnBookReq request) throws GeneralException {
        Optional<Member> memberOpt = memberRepository.findByEmail(request.getEmail());
        if (memberOpt.isPresent() == false) {
            throw new GeneralException("Email doesn't exist");
        }

        Optional<Copy> copyOpt = copyRepository.findById(request.getId());
        if (copyOpt.isPresent() == false) {
            throw new GeneralException("Book doesn't exist");
        }

        Optional<Loan> loanOpt = loanRepository.findLoanedCopy(request.getId());
        Copy copy = copyOpt.get();
        if (copy.isBorrowed() == false || loanOpt.isPresent() == false) {
            throw new GeneralException("Book has already been returned");
        }

        if (loanOpt.get().getMember().getEmail().equalsIgnoreCase(request.getEmail()) == false) {
            throw new GeneralException("Book is not borrowed under this email");
        }

        Loan loan = loanOpt.get();
        loan.setReturnDate(new Date(Instant.now().toEpochMilli()));

        copy.setBorrowed(false);
        copyRepository.save(copy);
        loanRepository.save(loan);

        return getReturnBookResMapper(copy, memberOpt, loan);
    }

    private static ReturnBookRes getReturnBookResMapper(Copy copy, Optional<Member> memberOpt, Loan loan) {
        ReturnBookRes returnBookRes = new ReturnBookRes();
        BookDto bookDto = new BookDto();

        bookDto.setIsbn(copy.getBook().getIsbn());
        bookDto.setAuthor(copy.getBook().getAuthor());
        bookDto.setTitle(copy.getBook().getTitle());
        bookDto.setId(copy.getId());

        BorrowerDto borrowerDto = new BorrowerDto();
        borrowerDto.setEmail(memberOpt.get().getEmail());
        borrowerDto.setName(memberOpt.get().getName());
        borrowerDto.setId(memberOpt.get().getId());

        returnBookRes.setBook(bookDto);
        returnBookRes.setIssueDate(loan.getIssueDate());
        returnBookRes.setDueDate(loan.getDueDate());
        returnBookRes.setId(loan.getId());
        returnBookRes.setBorrower(borrowerDto);
        returnBookRes.setReturnDate(loan.getReturnDate());
        return returnBookRes;
    }

    @Override
    public CreateMemberRes registerBorrower(CreateMemberReq request) throws GeneralException {
        Optional<Member> memberOpt = memberRepository.findByEmail(request.getEmail());

        if (memberOpt.isPresent()) {
            throw new GeneralException("Email is already registered");
        }

        Member member = new Member();
        member.setName(request.getName());
        member.setEmail(request.getEmail());

        memberRepository.save(member);

        CreateMemberRes response = new CreateMemberRes();
        response.setId(member.getId());
        response.setName(member.getName());
        response.setEmail(member.getEmail());

        return response;
    }


}
