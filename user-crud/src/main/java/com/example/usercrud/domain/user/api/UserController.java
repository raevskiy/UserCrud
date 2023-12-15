package com.example.usercrud.domain.user.api;

import com.example.usercrud.domain.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.example.usercrud.domain.user.api.UserMapper.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    @Transactional
    public CreateUserResponse createUser(@RequestBody @Valid CreateUserRequest request) {
        var response = toCreateUserResponse(userService.createUser(toUser(request)));
        attachLinkToResponse(response, response.getId());

        return response;
    }

    @PutMapping("/{userId}")
    @ResponseStatus(code = HttpStatus.OK)
    @Transactional
    public UserResponse updateUser(@RequestBody @Valid UpdateUserRequest request, @PathVariable UUID userId) {
        var response = toUserResponse(userService.updateUser(UserMapper.toUser(request, userId)));
        attachLinkToResponse(response, userId);

        return response;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(code = HttpStatus.OK)
    @Transactional
    public void deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(code = HttpStatus.OK)
    @Transactional
    public UserResponse getUser(@PathVariable UUID userId) {
        var response = toUserResponse(userService.findById(userId));
        attachLinkToResponse(response, userId);

        return response;
    }

    private void attachLinkToResponse(RepresentationModel<?> response, UUID id) {
        Link getLink = linkTo(UserController.class).slash(id).withSelfRel();
        response.add(getLink);
    }
}
