package com.aeon.library.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class GetBookReq {
    private String isbn;
    private String title;
    private String author;
    @Max(20)
    private Integer pageSize = 10;
    @Min(1)
    private Integer pageNo = 1;
}
