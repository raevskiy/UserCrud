package com.example.usercrud.domain.user.api;

import com.example.usercrud.domain.user.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    @Test
    void onValidArgumentsToUserCreatesEntityForCreation() {
        CreateUserRequest request = new CreateUserRequest(
                "Kim", "Kitsuragi", "kim.kitsuragi@rcm.org", LocalDate.now());

        User user = UserMapper.toUser(request);

        assertThat(user.getFirstName()).isEqualTo(request.firstName());
        assertThat(user.getLastName()).isEqualTo(request.lastName());
        assertThat(user.getEmail()).isEqualTo(request.email());
    }

    //There should be more tests here, but it is allowed to skip them according to the test assignment description
}
