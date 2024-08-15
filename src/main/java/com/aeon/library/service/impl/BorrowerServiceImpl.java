package com.aeon.library.service.impl;

import com.aeon.library.dto.*;
import com.aeon.library.entity.Book;
import com.aeon.library.entity.Borrower;
import com.aeon.library.entity.Borrowing;
import com.aeon.library.exception.GeneralException;
import com.aeon.library.repo.BookRepository;
import com.aeon.library.repo.BorrowerRepository;
import com.aeon.library.repo.BorrowingRepository;
import com.aeon.library.service.BorrowerService;
import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Service
public class BorrowerServiceImpl implements BorrowerService {
    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;
    private final BorrowingRepository borrowingRepository;
    private final DozerBeanMapper mapper;

    public BorrowerServiceImpl(BorrowerRepository borrowerRepository,
                               BookRepository bookRepository, BorrowingRepository borrowingRepository,
                               DozerBeanMapper dozerBeanMapper) {
        this.borrowerRepository = borrowerRepository;
        this.bookRepository = bookRepository;
        this.borrowingRepository = borrowingRepository;
        this.mapper = dozerBeanMapper;
    }

    @Override
    public BorrowBookRes borrowBook(Long borrowerId, BorrowBookReq request) throws GeneralException {
        // check for outstanding books
        // limit books borrowed to 5
        // ensure the book is not borrowed
        // check if book is currently borrowed

        Optional<Borrower> borrowerOpt = borrowerRepository.findByEmail(request.getEmail());
        if (borrowerOpt.isPresent() == false) {
            throw new GeneralException("Email doesn't exist");
        }

        Optional<Book> bookOpt = bookRepository.findById(request.getId());
        if (bookOpt.isPresent() == false) {
            throw new GeneralException("Book doesn't exist");
        }

        Optional<Borrowing> borrowingBorrowedStatusOpt = borrowingRepository.findBookInBorrowedStatus(request.getId());
        if (borrowingBorrowedStatusOpt.isPresent()) {
            throw new GeneralException("Book is currently borrowed");
        }

        Borrowing borrowing = new Borrowing();
        borrowing.setBorrower(borrowerOpt.get());
        borrowing.setBook(bookOpt.get());
        borrowing.setBorrowedDate(Timestamp.from(Instant.now()));
        borrowing.getBook().setBorrowed(true);

        borrowingRepository.save(borrowing);

        BorrowBookRes borrowBookRes = new BorrowBookRes();
        mapper.map(borrowing, borrowBookRes);

        return borrowBookRes;
    }

    @Override
    public BorrowBookRes returnBook(Long borrowerId, BorrowBookReq request) throws GeneralException {
        // calculate fines

        Optional<Borrower> borrowerOpt = borrowerRepository.findByEmail(request.getEmail());
        if (borrowerOpt.isPresent() == false) {
            throw new GeneralException("Email doesn't exist");
        }

        Optional<Book> bookOpt = bookRepository.findById(request.getId());
        if (bookOpt.isPresent() == false) {
            throw new GeneralException("Book doesn't exist");
        }

        Optional<Borrowing> borrowingBorrowedStatusOpt = borrowingRepository.findBookInBorrowedStatus(request.getId());
        if (borrowingBorrowedStatusOpt.isPresent() == false) {
            throw new GeneralException("Book has already been returned");
        }

        Borrowing borrowing = borrowingBorrowedStatusOpt.get();
        borrowing.setReturnedDate(Timestamp.from(Instant.now()));
        borrowing.getBook().setBorrowed(false);
        borrowingRepository.save(borrowing);

        BorrowBookRes borrowBookRes = new BorrowBookRes();
        mapper.map(borrowing, borrowBookRes);

        return borrowBookRes;
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
