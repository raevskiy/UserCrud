package com.example.usercrud.domain.user.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record CreateUserRequest(
        @NotNull
        String firstName,
        @NotNull
        String lastName,
        @NotNull
        @Email
        String email,
        @NotNull
        @PastOrPresent
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate birthday) {
}
