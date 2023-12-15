package com.example.usercrud.domain.user.api;

import com.example.usercrud.domain.user.User;

import java.util.UUID;

public class UserMapper {

    public static User toUser(CreateUserRequest createUserRequest) {
        User user = new User();
        user.setFirstName(createUserRequest.firstName());
        user.setLastName(createUserRequest.lastName());
        user.setEmail(createUserRequest.email());
        user.setBirthday(createUserRequest.birthday());

        return user;
    }

    public static CreateUserResponse toCreateUserResponse(User user) {
        return new CreateUserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.calculateAge(),
                user.getVersion());
    }

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.calculateAge(),
                user.getVersion());
    }

    public static User toUser(UpdateUserRequest updateUserRequest, UUID userId) {
        User user = new User();
        user.setFirstName(updateUserRequest.firstName());
        user.setLastName(updateUserRequest.lastName());
        user.setEmail(updateUserRequest.email());
        user.setBirthday(updateUserRequest.birthday());
        user.setVersion(updateUserRequest.version());
        user.setId(userId);

        return user;
    }

}
