package com.aeon.library.controller;

import com.aeon.library.dto.*;
import com.aeon.library.exception.GeneralException;
import com.aeon.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
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
    public GetBookRes getBooks(@Valid GetBookReq request) throws GeneralException {
        return bookService.getBooks(request);
    }
}
