package com.codegym.shoeshopmanager.controller;

import com.codegym.shoeshopmanager.model.CartItem;
import com.codegym.shoeshopmanager.model.Role;
import com.codegym.shoeshopmanager.model.User;
import com.codegym.shoeshopmanager.repository.RoleRepository;
import com.codegym.shoeshopmanager.service.CartService;
import com.codegym.shoeshopmanager.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;


@Controller

public class AuthController {

    @Autowired
    private IUserService userService;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CartService cartService;

    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
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
            List<CartItem> items = cartService.getCartItems(user);
            session.setAttribute("cartItemCount", items.size());
            session.removeAttribute("cart");
            session.setAttribute("cartItemCount", 0);
            return role.equals("ADMIN") ? "redirect:/admin/dashboard" : "redirect:/users";
        } else {
            model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") User user, Model model) {
        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "auth/register";
        }

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Role USER không tồn tại"));
        user.setRole(userRole);

        userService.save(user);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("currentUser");
        session.removeAttribute("cartItemCount");
        session.removeAttribute("cartItems");
        session.invalidate();
        return "redirect:/login";
    }
}


