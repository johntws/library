package com.aeon.library.dto;

import lombok.Data;

@Data
public class CreateMemberRes {
    private Long memberId;
    private String name;
    private String email;
}
