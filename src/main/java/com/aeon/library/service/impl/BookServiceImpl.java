package com.aeon.library.service.impl;

import com.aeon.library.dto.*;
import com.aeon.library.entity.Copy;
import com.aeon.library.entity.Book;
import com.aeon.library.exception.GeneralException;
import com.aeon.library.repo.BookRepository;
import com.aeon.library.repo.CopyRepository;
import com.aeon.library.service.BookService;
import com.aeon.library.specification.BookSpecification;
import com.aeon.library.util.BookUtil;
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
    private final CopyRepository copyRepository;
    private final BookRepository bookRepository;

    public BookServiceImpl(CopyRepository copyRepository,
                           BookRepository bookRepository) {
        this.copyRepository = copyRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public CreateBookRes registerBook(CreateBookReq request) throws GeneralException {
        if (BookUtil.validateISBN10(request.getIsbn()) == false) {
            throw new GeneralException("Invalid ISBN");
        }

        Book book;
        Copy copy = new Copy();
        Optional<Book> bookOpt = bookRepository.findById(request.getIsbn());

        if (bookOpt.isPresent()) {
            book = bookOpt.get();
            if (book.getAuthor().equalsIgnoreCase(request.getAuthor()) == false
                    || book.getTitle().equalsIgnoreCase(request.getTitle()) == false) {
                throw new GeneralException("Author and/or Title does not match existing ISBN");
            }
        } else {
            book = new Book();
            book.setIsbn(request.getIsbn());
            book.setAuthor(request.getAuthor());
            book.setTitle(request.getTitle());
            bookRepository.save(book);
        }

        copy.setBook(book);
        copyRepository.save(copy);

        CreateBookRes response = new CreateBookRes();
        response.setId(copy.getId());
        response.setAuthor(book.getAuthor());
        response.setTitle(book.getTitle());
        response.setIsbn(book.getIsbn());
        response.setBorrowed(copy.isBorrowed());

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
        Page<Book> bookPage = bookRepository.findAll(specification, pageRequest);
        List<Book> bookList = bookPage.get().toList();

        return getBookResMapper(bookList, bookPage);
    }

    private static GetBookRes getBookResMapper(List<Book> bookList, Page<Book> bookPage) {
        ArrayList<BookDto> bookDtoList = new ArrayList<>();
        for (Book book : bookList) {
            BookDto bookDto = new BookDto();
            bookDto.setAuthor(book.getAuthor());
            bookDto.setTitle(book.getTitle());
            bookDto.setIsbn(book.getIsbn());

            ArrayList<CopyDto> copyDtoList = new ArrayList<>();
            for (Copy copy : book.getCopies()) {
                CopyDto copyDto = new CopyDto();
                copyDto.setId(copy.getId());
                copyDto.setBorrowed(copy.isBorrowed());
                copyDtoList.add(copyDto);
            }

            bookDto.setCopies(copyDtoList);
            bookDtoList.add(bookDto);
        }

        GetBookRes response = new GetBookRes();
        response.setBookList(bookDtoList);
        response.setTotalPages(bookPage.getTotalPages());
        response.setCurrentPage(bookPage.getPageable().getPageNumber() + 1);
        return response;
    }
}
