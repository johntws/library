package com.aeon.library.service.impl;

import com.aeon.library.dto.*;
import com.aeon.library.entity.Book;
import com.aeon.library.entity.Copy;
import com.aeon.library.entity.Member;
import com.aeon.library.entity.Loan;
import com.aeon.library.exception.GeneralException;
import com.aeon.library.repo.CopyRepository;
import com.aeon.library.repo.MemberRepository;
import com.aeon.library.repo.LoanRepository;
import com.aeon.library.service.MemberService;
import com.aeon.library.util.DateUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.sql.Date;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {
    private final CopyRepository copyRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    public MemberServiceImpl(MemberRepository memberRepository,
                             CopyRepository copyRepository,
                             LoanRepository loanRepository) {
        this.memberRepository = memberRepository;
        this.copyRepository = copyRepository;
        this.loanRepository = loanRepository;
    }

    @Override
    public LoanBookRes borrowBook(Long borrowerId, LoanBookReq request) throws GeneralException {
        Date today = new Date(Instant.now().toEpochMilli());
        Date dueDate = DateUtil.stringToDate(request.getDueDate(), DateUtil.YYYY_MM_DD);
        if (dueDate.after(today) == false) {
            throw new GeneralException("Due date is before today");
        }

        Optional<Member> memberOpt = memberRepository.findByEmail(request.getEmail());
        if (memberOpt.isPresent() == false) {
            throw new GeneralException("Email doesn't exist");
        }

        Optional<Copy> copyOpt = copyRepository.findByIdAndDeletedFalse(request.getCopyId());
        if (copyOpt.isPresent() == false) {
            throw new GeneralException("Book doesn't exist");
        }

        Copy copy = copyOpt.get();
        if (copy.isBorrowed()) {
            throw new GeneralException("Book is currently borrowed");
        }

        Member member = memberOpt.get();
        Book book = copy.getBook();

        Loan loan = new Loan();
        loan.setMember(member);
        loan.setCopy(copy);
        loan.setIssueDate(today);
        loan.setDueDate(dueDate);
        loan.getCopy().setBorrowed(true);

        loanRepository.save(loan);

        return getBorrowBookResMapper(book, copy, member, loan);
    }

    private static LoanBookRes getBorrowBookResMapper(Book book, Copy copy, Member member, Loan loan) {
        LoanBookRes loanBookRes = new LoanBookRes();

        BookDto bookDto = new BookDto();
        bookDto.setIsbn(book.getIsbn());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setTitle(book.getTitle());
        bookDto.setCopyId(copy.getId());

        MemberDto memberDto = new MemberDto();
        memberDto.setEmail(member.getEmail());
        memberDto.setName(member.getName());
        memberDto.setMemberId(member.getId());

        loanBookRes.setBook(bookDto);
        loanBookRes.setIssueDate(loan.getIssueDate());
        loanBookRes.setDueDate(loan.getDueDate());
        loanBookRes.setLoanId(loan.getId());
        loanBookRes.setMember(memberDto);

        return loanBookRes;
    }

    @Override
    public ReturnBookRes returnBook(Long borrowerId, ReturnBookReq request) throws GeneralException {
        Optional<Member> memberOpt = memberRepository.findByEmail(request.getEmail());
        if (memberOpt.isPresent() == false) {
            throw new GeneralException("Email doesn't exist");
        }

        Member member = memberOpt.get();
        Optional<Copy> copyOpt = copyRepository.findById(request.getCopyId());
        if (copyOpt.isPresent() == false) {
            throw new GeneralException("Book doesn't exist");
        }

        Optional<Loan> loanOpt = loanRepository.findLoanedCopy(request.getCopyId());
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

        return getReturnBookResMapper(copy, member, loan);
    }

    private static ReturnBookRes getReturnBookResMapper(Copy copy, Member member, Loan loan) {
        ReturnBookRes returnBookRes = new ReturnBookRes();
        BookDto bookDto = new BookDto();

        bookDto.setIsbn(copy.getBook().getIsbn());
        bookDto.setAuthor(copy.getBook().getAuthor());
        bookDto.setTitle(copy.getBook().getTitle());
        bookDto.setCopyId(copy.getId());

        MemberDto memberDto = new MemberDto();
        memberDto.setEmail(member.getEmail());
        memberDto.setName(member.getName());
        memberDto.setMemberId(member.getId());

        returnBookRes.setBook(bookDto);
        returnBookRes.setIssueDate(loan.getIssueDate());
        returnBookRes.setDueDate(loan.getDueDate());
        returnBookRes.setLoanId(loan.getId());
        returnBookRes.setMember(memberDto);
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
        response.setMemberId(member.getId());
        response.setName(member.getName());
        response.setEmail(member.getEmail());

        return response;
    }


}
