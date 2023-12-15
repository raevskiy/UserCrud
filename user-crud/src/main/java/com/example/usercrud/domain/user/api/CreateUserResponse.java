package com.example.usercrud.domain.user.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class CreateUserResponse extends RepresentationModel<CreateUserResponse> {
    private final UUID id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final long age;
    private final long version;
}
