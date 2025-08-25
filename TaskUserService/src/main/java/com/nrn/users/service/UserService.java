package com.nrn.users.service;

import java.util.List;

import com.nrn.users.model.User;

public interface UserService {
    public User getUserByEmail(String email);
    public User getUserById(Long userId);
    public List<User> getAllUsers();
}