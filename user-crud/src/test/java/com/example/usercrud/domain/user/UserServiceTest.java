package com.example.usercrud.domain.user;

import com.example.usercrud.exception.EntityNotFoundProblem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService service;
    @Mock
    private UserRepository analysisRepository;

    @Test
    void onUserExistsInRepositoryFindByIdReturnsUser() {
        var id = UUID.randomUUID();
        User user = new User();
        when(analysisRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.of(user));

        assertEquals(user, service.findById(id));
    }

    @Test
    void onUserAbsentInRepositoryFindByIdThrowsProblem() {
        var id = UUID.randomUUID();
        User user = new User();
        when(analysisRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundProblem.class,
                () -> service.findById(id),
                String.format("Entity User where id = %s not found", id));
    }

    //There should be more tests here, but it is allowed to skip them according to the test assignment description
}
