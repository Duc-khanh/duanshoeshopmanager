package com.codegym.shoeshopmanager.controller;

import com.codegym.shoeshopmanager.model.Product;
import com.codegym.shoeshopmanager.model.User;
import com.codegym.shoeshopmanager.service.IProductService;
import com.codegym.shoeshopmanager.service.IUserService;
import com.codegym.shoeshopmanager.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/users")
public class HomeUserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private IProductService productService;


    @GetMapping("")
    public String dashboard(HttpSession session, Model model) {
        List<Product> productList = productService.findAll();
        model.addAttribute("productList", productList);

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            model.addAttribute("user", currentUser);
        }

        return "users/homeUser";
    }

    @GetMapping("/create")
    public String showFormNewUser (Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.findAll());
        return "admin/manageUser/add_user";
    }
    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user) {
        userService.save(user);
        return "redirect:/admin/user";
    }
    @GetMapping("edit/{id}")
    public String showEditFormUser(@PathVariable Integer id , Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.findAll());
        return "admin/manageUser/update_user";
    }
    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user) {
        userService.save(user);
        return "redirect:/admin/user";
    }
    @GetMapping("view/{id}")
    public String showViewFormUser(@PathVariable Integer id , Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.findAll());
        return "admin/manageUser/view_user";
    }
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id) {
        userService.delete(id);
        return "redirect:/admin/user";
    }
    @GetMapping("search")
    public String searchUsers(@RequestParam("username") String keyword, Model model) {
        List<User> users = userService.searchByName(keyword);
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        return "admin/manageUser/user_list";
    }


}
