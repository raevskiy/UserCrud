package com.example.usercrud.domain.user;

import com.example.usercrud.exception.EntityNotFoundProblem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        verifyUserExists(user.getId());
        return userRepository.saveAndFlush(user);
    }

    public void deleteUser(UUID userId) {
        verifyUserExists(userId);
        userRepository.softDeleteUser(userId);
    }

    public User findById(UUID userId) {
        return userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() -> new EntityNotFoundProblem("User", "id", userId.toString()));
    }

    private void verifyUserExists(UUID userId) {
        if (userRepository.isUserPresent(userId) != Boolean.TRUE) {
            throw new EntityNotFoundProblem("User", "id", userId.toString());
        }
    }
}
