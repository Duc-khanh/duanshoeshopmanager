package com.codegym.shoeshopmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/homeUser")
    public String homeUser() {
        return "homeUser";
    }

}
