package com.t1impulse.interviewer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.t1impulse.interviewer.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}