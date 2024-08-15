package com.aeon.library.service.impl;

import com.aeon.library.dto.*;
import com.aeon.library.entity.BookCopy;
import com.aeon.library.entity.Book;
import com.aeon.library.exception.GeneralException;
import com.aeon.library.repo.BookRepository;
import com.aeon.library.repo.BookCopyRepository;
import com.aeon.library.service.BookService;
import com.aeon.library.specification.BookSpecification;
import com.aeon.library.util.IsbnUtil;
import org.dozer.DozerBeanMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
    private final BookCopyRepository bookCopyRepository;
    private final DozerBeanMapper mapper;
    private final BookRepository bookRepository;

    public BookServiceImpl(BookCopyRepository bookCopyRepository,
                           DozerBeanMapper mapper,
                           BookRepository bookRepository) {
        this.bookCopyRepository = bookCopyRepository;
        this.mapper = mapper;
        this.bookRepository = bookRepository;
    }

    @Override
    public CreateBookRes registerBook(CreateBookReq request) throws GeneralException {
        if (IsbnUtil.validateISBN10(request.getIsbn()) == false) {
            throw new GeneralException("Invalid ISBN");
        }

        Optional<Book> bookDetailsOpt = bookRepository.findById(request.getIsbn());

        Book book;
        BookCopy bookCopy = new BookCopy();

        if (bookDetailsOpt.isPresent()) {
            book = bookDetailsOpt.get();
            if (book.getAuthor().equalsIgnoreCase(request.getAuthor()) == false
                    || book.getTitle().equalsIgnoreCase(request.getTitle()) == false) {
                throw new GeneralException("Author and/or Title does not match existing ISBN");
            }
        } else {
            book = new Book();
            book.setIsbn(request.getIsbn());
            book.setAuthor(request.getAuthor());
            book.setTitle(request.getTitle());
        }

        bookCopy.setBook(book);
        bookRepository.save(book);
        bookCopyRepository.save(bookCopy);

        CreateBookRes response = new CreateBookRes();
        response.setId(bookCopy.getId());
        response.setAuthor(book.getAuthor());
        response.setTitle(book.getTitle());
        response.setIsbn(book.getIsbn());
        response.setBorrowed(bookCopy.isBorrowed());

        return response;
    }

    @Override
    public GetBookRes getBooks(GetBookReq request) {
        Specification<Book> specification = Specification
                .where(BookSpecification.likeIsbn(request.getIsbn()))
                .and(BookSpecification.likeAuthor(request.getAuthor()))
                .and(BookSpecification.likeTitle(request.getTitle()))
                .and(BookSpecification.equalDeleted(false));

        request.setPageNo(request.getPageNo() - 1);
        PageRequest pageRequest = PageRequest.of(request.getPageNo(), request.getPageSize());

        Page<Book> bookPages = bookRepository.findAll(specification, pageRequest);
        List<Book> bookCopyList = bookPages.get().toList();

        ArrayList<BookDto> bookDtoList = new ArrayList<>();
        for (Book book : bookCopyList) {
            BookDto bookDto = new BookDto();
            bookDto.setAuthor(book.getAuthor());
            bookDto.setTitle(book.getTitle());
            bookDto.setIsbn(book.getIsbn());

            ArrayList<BookCopyDto> bookCopyDtoList = new ArrayList<>();
            for (BookCopy bookCopy : book.getBookCopies()) {
                BookCopyDto bookCopyDto = new BookCopyDto();
                bookCopyDto.setId(bookCopy.getId());
                bookCopyDto.setBorrowed(bookCopy.isBorrowed());
                bookCopyDtoList.add(bookCopyDto);
            }

            bookDto.setCopies(bookCopyDtoList);
            bookDtoList.add(bookDto);
        }

        GetBookRes response = new GetBookRes();
        response.setBookList(bookDtoList);
        response.setTotalPages(bookPages.getTotalPages());
        response.setCurrentPage(bookPages.getPageable().getPageNumber() + 1);

        return response;
    }
}
