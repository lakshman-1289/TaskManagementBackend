package com.nrn.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nrn.users.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByEmail(String email);
}