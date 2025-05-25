package com.codegym.shoeshopmanager.controller;

import com.codegym.shoeshopmanager.model.Product;
import com.codegym.shoeshopmanager.model.User;
import com.codegym.shoeshopmanager.service.CategoryService;
import com.codegym.shoeshopmanager.service.IProductService;
import com.codegym.shoeshopmanager.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private IProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private IUserService userService;
    @Value("${file-upload}")
    private String uploadDir;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !currentUser.getRole().getRoleName().equals("ADMIN")) {
            return "redirect:/login";
        }
        model.addAttribute("admin", currentUser);
        return "admin/homeAdmin";
    }

    @GetMapping("/products")
    public String listProducts(Model model, @RequestParam(required = false) String keyword) {
        List<Product> products = (keyword != null)
                ? productService.searchByName(keyword)
                : productService.findAll();
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "admin/manageProduct/product_list";
    }

    @GetMapping("/user")
    public String homeUser(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin/manageUser/user_list";
    }

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "admin/manageCategory/category_list";
    }


}
