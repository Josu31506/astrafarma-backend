package com.example.astrafarma.User.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SigninRequest {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}