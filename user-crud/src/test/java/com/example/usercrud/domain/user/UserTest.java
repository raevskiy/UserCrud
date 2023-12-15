package com.example.usercrud.domain.user;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void onUserWithValidBirthdayShouldCalculateAge() {
        var user = new User(
                "Harrier",
                "Du Bois",
                "harrier.dubois@rcm.org",
                LocalDate.now().minusYears(30));

        assertEquals(30, user.calculateAge());
    }

    @Test
    void onUserWithInvalidBirthdayInFutureShouldCalculateNegativeAge() {
        var user = new User(
                "Harrier",
                "Du Bois",
                "harrier.dubois@rcm.org",
                LocalDate.now().plusYears(30));

        assertEquals(-30, user.calculateAge());
    }

}
