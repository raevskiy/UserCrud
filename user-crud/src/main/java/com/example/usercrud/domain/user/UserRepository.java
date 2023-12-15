package com.example.usercrud.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    @Query(value = """
             SELECT TRUE FROM "user" WHERE EXISTS
              (SELECT 1 FROM "user" p WHERE p.id = :userId AND p.active = true)
             """, nativeQuery = true)
    Boolean isUserPresent(UUID userId);

    Optional<User> findByIdAndActiveTrue(UUID userId);

    @Modifying
    @Query(value = """
             UPDATE "user" SET active = false WHERE id = :userId
             """, nativeQuery = true)
    void softDeleteUser(UUID userId);


}
