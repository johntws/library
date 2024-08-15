package com.aeon.library.controller;

import com.aeon.library.dto.*;
import com.aeon.library.exception.GeneralException;
import com.aeon.library.service.BookService;
import com.aeon.library.service.BorrowerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public CreateBookRes registerBook(@Valid @RequestBody CreateBookReq request) throws GeneralException {
        return bookService.registerBook(request);
    }

    @GetMapping
    public ResponseEntity<GetBookRes> getBooks(@Valid GetBookReq request) throws GeneralException {
        GetBookRes body = bookService.getBooks(request);
        return new ResponseEntity<>(body, HttpStatus.ACCEPTED);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable @Valid @NotNull Long bookId) throws GeneralException {
        bookService.deleteBook(bookId);
        return null;
    }

    @PutMapping("/{id}")
    public CreateBookRes updateBook(@PathVariable @Valid @NotNull Long bookId, @Valid @RequestBody CreateBookReq request) throws GeneralException {
        return bookService.updateBook(bookId, request);
    }


}
