package com.aeon.library.dto;

import lombok.Data;

import java.util.List;

@Data
public class GetBookRes {
    private Integer totalPages;
    private Integer currentPage;
    private List<BookDto> bookList;
}
