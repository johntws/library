package com.aeon.library.service.impl;

import com.aeon.library.dto.*;
import com.aeon.library.entity.Book;
import com.aeon.library.exception.GeneralException;
import com.aeon.library.repo.BookRepository;
import com.aeon.library.service.BookService;
import com.aeon.library.specification.BookSpecification;
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
    private final BookRepository bookRepository;
    private final DozerBeanMapper mapper;

    public BookServiceImpl(BookRepository bookRepository, DozerBeanMapper mapper) {
        this.bookRepository = bookRepository;
        this.mapper = mapper;
    }

    @Override
    public CreateBookRes registerBook(CreateBookReq request) throws GeneralException {
        Optional<Book> bookOpt = bookRepository.findByIsbn(request.getIsbn());

        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            if (book.getBookDetails().getAuthor().equalsIgnoreCase(request.getAuthor()) == false
                    || book.getBookDetails().getTitle().equalsIgnoreCase(request.getTitle()) == false) {
                throw new GeneralException("Author and/or Title does not match existing ISBN");
            }
        }

        Book book = new Book();
        book.getBookDetails().setIsbn(request.getIsbn());
        book.getBookDetails().setAuthor(request.getAuthor());
        book.getBookDetails().setTitle(request.getTitle());

        bookRepository.save(book);

        CreateBookRes response = new CreateBookRes();
        response.setId(book.getId());
        response.setAuthor(book.getBookDetails().getAuthor());
        response.setTitle(book.getBookDetails().getTitle());
        response.setIsbn(book.getBookDetails().getIsbn());
        response.setBorrowed(false);

        return response;
    }

    @Override
    public GetBookRes getBooks(GetBookReq request) {
        Specification<Book> specification = Specification
                .where(BookSpecification.likeIsbn(request.getIsbn()))
                .and(BookSpecification.likeAuthor(request.getAuthor()))
                .and(BookSpecification.likeTitle(request.getTitle()));

        request.setPageNo(request.getPageNo() - 1);
        PageRequest pageRequest = PageRequest.of(request.getPageNo(), request.getPageSize());

        Page<Book> bookPages = bookRepository.findAll(specification, pageRequest);
        List<Book> bookList = bookPages.get().toList();

        ArrayList<BookDto> bookDtoList = new ArrayList<>();
        for (Book book : bookList) {
            BookDto bookDto = new BookDto();
            mapper.map(book, bookDto);
            bookDtoList.add(bookDto);
        }

        GetBookRes response = new GetBookRes();
        response.setBookList(bookDtoList);
        response.setTotalPages(bookPages.getTotalPages());
        response.setCurrentPage(bookPages.getPageable().getPageNumber() + 1);

        return response;
    }

    @Override
    public void deleteBook(Long id) {
        Optional<Book> bookOpt = bookRepository.findById(id);

        if (bookOpt.isEmpty() || bookOpt.get().isDeleted()) {
            throw new GeneralException("Book does not exist");
        }

        Book book = bookOpt.get();
        if (book.isBorrowed()) {
            throw new GeneralException("Book is currently borrowed");
        }

        book.setDeleted(true);
        bookRepository.save(book);
    }

    @Override
    public CreateBookRes updateBook(Long bookId, CreateBookReq request) throws GeneralException {
//        Optional<Book> bookOpt = bookRepository.findByIdAndDeletedFalse(bookId);
//
//        if (bookOpt.isEmpty()) {
//            throw new GeneralException("Book doesn't exist");
//        }
//
//        Book book = bookOpt.get();
//        book.setIsbn(request.getIsbn());
//        book.setAuthor(request.getAuthor());
//        book.setTitle(request.getTitle());
//
//        bookRepository.save(book);

        CreateBookRes response = new CreateBookRes();
//        response.setId(book.getId());
//        response.setAuthor(book.getAuthor());
//        response.setTitle(book.getTitle());
//        response.setIsbn(book.getIsbn());
//        response.setBorrowed(book.isBorrowed());

        return response;
    }
}
