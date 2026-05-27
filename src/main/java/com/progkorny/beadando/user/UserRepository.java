package com.progkorny.beadando.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
// DB muveletek a user entitasra
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}