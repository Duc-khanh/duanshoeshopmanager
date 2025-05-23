package com.codegym.shoeshopmanager.service;


import com.codegym.shoeshopmanager.model.User;

public interface IUserService {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    void save(User user);

    boolean isUsernameTaken(String username);

    User registerUser(String username, String password, String email);

    User login(String username, String password);
}