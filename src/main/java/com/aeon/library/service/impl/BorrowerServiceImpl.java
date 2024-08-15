package com.aeon.library.service.impl;

import com.aeon.library.dto.*;
import com.aeon.library.entity.BookCopy;
import com.aeon.library.entity.Borrower;
import com.aeon.library.entity.Borrowing;
import com.aeon.library.exception.GeneralException;
import com.aeon.library.repo.BookCopyRepository;
import com.aeon.library.repo.BorrowerRepository;
import com.aeon.library.repo.BorrowingRepository;
import com.aeon.library.service.BorrowerService;
import com.aeon.library.util.DateUtil;
import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Service
public class BorrowerServiceImpl implements BorrowerService {
    private final BookCopyRepository bookCopyRepository;
    private final BorrowerRepository borrowerRepository;
    private final BorrowingRepository borrowingRepository;
    private final DozerBeanMapper mapper;

    public BorrowerServiceImpl(BorrowerRepository borrowerRepository,
                               BookCopyRepository bookCopyRepository, BorrowingRepository borrowingRepository,
                               DozerBeanMapper dozerBeanMapper) {
        this.borrowerRepository = borrowerRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.borrowingRepository = borrowingRepository;
        this.mapper = dozerBeanMapper;
    }

    @Override
    public BorrowBookRes borrowBook(Long borrowerId, BorrowBookReq request) throws GeneralException {
        // check for outstanding books
        // limit books borrowed to 5

        Timestamp today = Timestamp.from(Instant.now());
        Timestamp dueDate = DateUtil.stringToTimestamp(request.getDueDate(), DateUtil.YYYY_MM_DD);
        if (dueDate.after(today) == false) {
            throw new GeneralException("Due date is before today");
        }

        Optional<Borrower> borrowerOpt = borrowerRepository.findByEmail(request.getEmail());
        if (borrowerOpt.isPresent() == false) {
            throw new GeneralException("Email doesn't exist");
        }

        Optional<BookCopy> bookOpt = bookCopyRepository.findByIdAndDeletedFalse(request.getId());
        if (bookOpt.isPresent() == false) {
            throw new GeneralException("Book doesn't exist");
        }

        BookCopy bookCopy = bookOpt.get();

        if (bookCopy.isBorrowed()) {
            throw new GeneralException("Book is currently borrowed");
        }

        Borrowing borrowing = new Borrowing();
        borrowing.setBorrower(borrowerOpt.get());
        borrowing.setBookCopy(bookCopy);
        borrowing.setIssueDate(today);
        borrowing.setDueDate(dueDate);
        borrowing.getBookCopy().setBorrowed(true);

        borrowingRepository.save(borrowing);

        BorrowBookRes borrowBookRes = new BorrowBookRes();
        BookDto bookDto = new BookDto();
        mapper.map(bookCopy.getBook(), bookDto);
        mapper.map(borrowing, borrowBookRes);
        borrowBookRes.setBook(bookDto);

        return borrowBookRes;
    }

    @Override
    public ReturnBookRes returnBook(Long borrowerId, ReturnBookReq request) throws GeneralException {
        Optional<Borrower> borrowerOpt = borrowerRepository.findByEmail(request.getEmail());
        if (borrowerOpt.isPresent() == false) {
            throw new GeneralException("Email doesn't exist");
        }

        Optional<BookCopy> bookCopyOpt = bookCopyRepository.findById(request.getId());
        if (bookCopyOpt.isPresent() == false) {
            throw new GeneralException("Book doesn't exist");
        }

        Optional<Borrowing> borrowingOpt = borrowingRepository.findBookInBorrowedStatus(request.getId());
        BookCopy bookCopy = bookCopyOpt.get();
        if (bookCopy.isBorrowed() == false || borrowingOpt.isPresent() == false) {
            throw new GeneralException("Book has already been returned");
        }

        if (borrowingOpt.get().getBorrower().getEmail().equalsIgnoreCase(request.getEmail()) == false) {
            throw new GeneralException("Book is not borrowed under this email");
        }

        Borrowing borrowing = borrowingOpt.get();
        borrowing.setReturnDate(Timestamp.from(Instant.now()));

        bookCopy.setBorrowed(false);
        bookCopyRepository.save(bookCopy);
        borrowingRepository.save(borrowing);

        ReturnBookRes returnBookRes = new ReturnBookRes();
        BookDto bookDto = new BookDto();
        mapper.map(bookCopy.getBook(), bookDto);
        mapper.map(borrowing, returnBookRes);
        returnBookRes.setBook(bookDto);

        return returnBookRes;
    }

    @Override
    public CreateBorrowerRes registerBorrower(CreateBorrowerReq request) throws GeneralException {
        Optional<Borrower> emailOpt = borrowerRepository.findByEmail(request.getEmail());

        if (emailOpt.isPresent()) {
            throw new GeneralException("Email is already registered");
        }

        Borrower borrower = new Borrower();
        borrower.setName(request.getName());
        borrower.setEmail(request.getEmail());

        borrowerRepository.save(borrower);

        CreateBorrowerRes response = new CreateBorrowerRes();
        response.setId(borrower.getId());
        response.setName(borrower.getName());
        response.setEmail(borrower.getEmail());

        return response;
    }


}
