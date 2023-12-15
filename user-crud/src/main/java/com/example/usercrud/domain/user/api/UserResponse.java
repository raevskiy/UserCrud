package com.example.usercrud.domain.user.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Getter
@RequiredArgsConstructor
public class UserResponse extends RepresentationModel<UserResponse> {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final long age;
    private final long version;
}
