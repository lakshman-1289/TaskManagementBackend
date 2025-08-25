package com.nrn.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nrn.auth.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByEmail(String email);
}