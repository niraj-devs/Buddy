package com.buddy.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterResponse {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String message;
}