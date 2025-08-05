package com.nrn.users.service;

import java.util.List;

import com.nrn.users.model.User;

public interface UserService {
    public User getUserProfileByJwt(String jwt);
    public List<User> getAllUsers();
}