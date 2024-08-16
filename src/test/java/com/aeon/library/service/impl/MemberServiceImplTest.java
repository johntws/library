package com.aeon.library.service.impl;

import com.aeon.library.dto.*;
import com.aeon.library.entity.Book;
import com.aeon.library.entity.Copy;
import com.aeon.library.entity.Loan;
import com.aeon.library.entity.Member;
import com.aeon.library.exception.GeneralException;
import com.aeon.library.repo.CopyRepository;
import com.aeon.library.repo.LoanRepository;
import com.aeon.library.repo.MemberRepository;
import com.aeon.library.util.DateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.sql.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberServiceImplTest {

    @Mock
    private CopyRepository copyRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBorrowBookSuccess() throws GeneralException {
        Date due = new Date(Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dueDateStr = formatter.format(due);

        BorrowBookReq request = new BorrowBookReq();
        request.setEmail("test@example.com");
        request.setDueDate(dueDateStr);
        request.setId(1L);

        Date today =  new Date(Instant.now().toEpochMilli());
        Date dueDate = DateUtil.stringToDate(request.getDueDate(), DateUtil.YYYY_MM_DD);

        Member member = new Member();
        member.setEmail("test@example.com");
        member.setId(1L);
        member.setName("tester");

        Book book = new Book();
        book.setIsbn("123213");
        book.setTitle("Title");
        book.setAuthor("Author");

        Copy copy = new Copy();
        copy.setId(1L);
        copy.setBorrowed(false);
        copy.setBook(book);

        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(member));
        when(copyRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(copy));

        BorrowBookRes response = memberService.borrowBook(1L, request);

        assertNotNull(response);
        assertEquals("123213", response.getBook().getIsbn());
        assertEquals("test@example.com", response.getBorrower().getEmail());
        assertEquals(today.toString(), response.getIssueDate().toString());
        assertEquals(dueDate, response.getDueDate());

        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void testBorrowBookFailDueToInvalidEmail() {
        Date due = new Date(Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dueDateStr = formatter.format(due);

        BorrowBookReq request = new BorrowBookReq();
        request.setEmail("invalid@example");
        request.setDueDate(dueDateStr);

        when(memberRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        assertThrows(GeneralException.class, () -> memberService.borrowBook(1L, request));
    }

    @Test
    void testBorrowBookFailDueToWrongBook() {
        Date due = new Date(Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dueDateStr = formatter.format(due);

        BorrowBookReq request = new BorrowBookReq();
        request.setEmail("test@example.com");
        request.setDueDate(dueDateStr);

        Member member = new Member();
        member.setEmail("test@example.com");
        member.setId(1L);
        member.setName("tester");

        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(member));
        when(copyRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(GeneralException.class, () -> memberService.borrowBook(1L, request));
    }

    @Test
    void testBorrowBookFailDueToBookBorrowed() {
        Date due = new Date(Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dueDateStr = formatter.format(due);

        BorrowBookReq request = new BorrowBookReq();
        request.setEmail("test@example.com");
        request.setDueDate(dueDateStr);
        request.setId(1L);

        Member member = new Member();
        member.setEmail("test@example.com");
        member.setId(1L);
        member.setName("tester");

        Book book = new Book();
        book.setIsbn("123213");
        book.setTitle("Title");
        book.setAuthor("Author");

        Copy copy = new Copy();
        copy.setId(1L);
        copy.setBorrowed(true);
        copy.setBook(book);

        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(member));
        when(copyRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(copy));

        assertThrows(GeneralException.class, () -> memberService.borrowBook(1L, request));
    }

    @Test
    void testBorrowBookFailDueToInvalidDate() {
        BorrowBookReq request = new BorrowBookReq();
        request.setEmail("invalid@example.com");
        request.setDueDate("2023-12-31");

        when(memberRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        assertThrows(GeneralException.class, () -> memberService.borrowBook(1L, request));
    }

    @Test
    void testReturnBookSuccess() throws GeneralException {
        ReturnBookReq request = new ReturnBookReq();
        request.setEmail("test@example.com");
        request.setId(1L);

        Member member = new Member();
        member.setEmail("test@example.com");
        member.setId(1L);
        member.setName("tester");

        Book book = new Book();
        book.setIsbn("123213");
        book.setTitle("Title");
        book.setAuthor("Author");

        Copy copy = new Copy();
        copy.setId(1L);
        copy.setBorrowed(true);
        copy.setBook(book);

        Loan loan = new Loan();
        loan.setCopy(copy);
        loan.setMember(member);
        loan.setIssueDate(new Date(Instant.now().toEpochMilli()));
        loan.setReturnDate(new Date(Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli()));

        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(member));
        when(copyRepository.findById(1L)).thenReturn(Optional.of(copy));
        when(loanRepository.findLoanedCopy(1L)).thenReturn(Optional.of(loan));

        ReturnBookRes expectedResponse = new ReturnBookRes();

        ReturnBookRes response = memberService.returnBook(1L, request);

        assertNotNull(response);
        assertEquals("123213", response.getBook().getIsbn());
        assertEquals("test@example.com", response.getBorrower().getEmail());
        assertNotNull(response.getIssueDate());
        assertNotNull(response.getReturnDate());

        verify(copyRepository, times(1)).save(any(Copy.class));
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void testReturnBookFailDueToAlreadyReturned() {
        ReturnBookReq request = new ReturnBookReq();
        request.setEmail("test@example.com");
        request.setId(1L);

        Copy copy = new Copy();
        copy.setId(1L);
        copy.setBorrowed(false);

        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new Member()));
        when(copyRepository.findById(1L)).thenReturn(Optional.of(copy));

        assertThrows(GeneralException.class, () -> memberService.returnBook(1L, request));
    }

    @Test
    void testReturnBookFailDueToInvalidEmail() {
        ReturnBookReq request = new ReturnBookReq();
        request.setEmail("invalid@example.com");
        request.setId(1L);

        Copy copy = new Copy();
        copy.setId(1L);
        copy.setBorrowed(false);

        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new Member()));
        when(copyRepository.findById(1L)).thenReturn(Optional.of(copy));

        assertThrows(GeneralException.class, () -> memberService.returnBook(1L, request));
    }

    @Test
    void testReturnBookFailDueToInvalidBook() {
        ReturnBookReq request = new ReturnBookReq();
        request.setEmail("test@example.com");
        request.setId(2L);

        Copy copy = new Copy();
        copy.setId(1L);
        copy.setBorrowed(false);

        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new Member()));
        when(copyRepository.findById(1L)).thenReturn(Optional.of(copy));

        assertThrows(GeneralException.class, () -> memberService.returnBook(1L, request));
    }

    @Test
    void testReturnBookFailDueToInvalidUserAndBook() {
        ReturnBookReq request = new ReturnBookReq();
        request.setEmail("test@example.com");
        request.setId(1L);

        Member member = new Member();
        member.setEmail("aaa@example.com");
        member.setId(1L);
        member.setName("tester");

        Book book = new Book();
        book.setIsbn("123213");
        book.setTitle("Title");
        book.setAuthor("Author");

        Copy copy = new Copy();
        copy.setId(1L);
        copy.setBorrowed(true);
        copy.setBook(book);

        Loan loan = new Loan();
        loan.setCopy(copy);
        loan.setMember(member);
        loan.setIssueDate(new Date(Instant.now().toEpochMilli()));
        loan.setReturnDate(new Date(Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli()));

        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new Member()));
        when(copyRepository.findById(1L)).thenReturn(Optional.of(copy));
        when(loanRepository.findLoanedCopy(1L)).thenReturn(Optional.of(loan));

        assertThrows(GeneralException.class, () -> memberService.returnBook(1L, request));
    }

    @Test
    void testRegisterBorrowerSuccess() throws GeneralException {
        CreateMemberReq request = new CreateMemberReq();
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");

        when(memberRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());

        Member member = new Member();
        member.setId(1L);
        member.setName("John Doe");
        member.setEmail("john.doe@example.com");

        when(memberRepository.save(any(Member.class))).thenReturn(member);

        CreateMemberRes response = memberService.registerBorrower(request);

        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        assertEquals("john.doe@example.com", response.getEmail());
    }

    @Test
    void testRegisterBorrowerFailDueToExistingEmail() {
        CreateMemberReq request = new CreateMemberReq();
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");

        Member existingMember = new Member();
        existingMember.setEmail("john.doe@example.com");

        when(memberRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(existingMember));

        assertThrows(GeneralException.class, () -> memberService.registerBorrower(request));
    }


}