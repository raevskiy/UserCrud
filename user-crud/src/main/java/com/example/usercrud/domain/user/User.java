package com.example.usercrud.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Table(name = "\"user\"")
public class User {

    @Id
    @Column(unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDate birthday;

    @Version
    private Long version;

    @Column(nullable = false)
    private Boolean active = true;

    public User() {
        super();
    }

    public User(String firstName, String lastName, String email, LocalDate birthday) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthday = birthday;
        this.active = true;
    }

    public long calculateAge() {
        return ChronoUnit.YEARS.between(birthday, LocalDate.now());
    }

}
