package com.codegym.shoeshopmanager.service;

import com.codegym.shoeshopmanager.model.Role;
import com.codegym.shoeshopmanager.model.User;
import com.codegym.shoeshopmanager.repository.RoleRepository;
import com.codegym.shoeshopmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    public User registerUser(String username, String password, String email) {
        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Role USER not found"));

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password); // NÊN mã hoá với BCrypt!
        newUser.setEmail(email);
        newUser.setRole(userRole);

        return userRepository.save(newUser);
    }

    public User login(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user.get();
        }
        return null;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}

