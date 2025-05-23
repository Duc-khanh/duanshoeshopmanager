package com.codegym.shoeshopmanager.controller;

import com.codegym.shoeshopmanager.model.Role;
import com.codegym.shoeshopmanager.model.User;
import com.codegym.shoeshopmanager.repository.RoleRepository;
import com.codegym.shoeshopmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;


    @Controller

    public class AuthController {

        @Autowired
        private UserService userService;

        @Autowired
        private RoleRepository roleRepository;

        @GetMapping("/login")
        public String loginForm() {
            return "login";
        }

        @PostMapping("/login")
        public String login(@RequestParam String username,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {
            User user = userService.login(username, password);
            if (user != null) {
                session.setAttribute("currentUser", user);
                String role = user.getRole().getRoleName();
                return role.equals("ADMIN") ? "redirect:/admin/dashboard" : "redirect:/homeUser";
            } else {
                model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
                return "login";
            }
        }

        @GetMapping("/register")
        public String registerForm(Model model) {
            model.addAttribute("user", new User());
            return "register";
        }

        @PostMapping("/register")
        public String register(@ModelAttribute("user") User user, Model model) {
            if (userService.existsByUsername(user.getUsername())) {
                model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
                return "register";
            }

            Role userRole = roleRepository.findByRoleName("USER")
                    .orElseThrow(() -> new RuntimeException("Role USER không tồn tại"));
            user.setRole(userRole);

            userService.save(user);
            return "redirect:/login";
        }

        @GetMapping("/logout")
        public String logout(HttpSession session) {
            session.invalidate();
            return "redirect:/login";
        }
    }


