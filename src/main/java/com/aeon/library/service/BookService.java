package com.aeon.library.service;

import com.aeon.library.dto.*;
import com.aeon.library.exception.GeneralException;

public interface BookService {
    CreateBookRes registerBook(CreateBookReq request) throws GeneralException;
    GetBookRes getBooks(GetBookReq request);
    void deleteBook(Long id);
    CreateBookRes updateBook(Long id, CreateBookReq request) throws GeneralException;
}
