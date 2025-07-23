package com.codegym.shoeshopmanager.controller;

import com.codegym.shoeshopmanager.model.Product;
import com.codegym.shoeshopmanager.model.User;
import com.codegym.shoeshopmanager.service.CategoryService;
import com.codegym.shoeshopmanager.service.IProductService;
import com.codegym.shoeshopmanager.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    @ModelAttribute
    public void addCurrentUserToModel(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            model.addAttribute("admin", currentUser);
        }
    }
    @GetMapping("/products")
    public String listProducts(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size,
                               @RequestParam(required = false) String keyword) {
        Page<Product> productPage;

        if (keyword != null && !keyword.isEmpty()) {
            productPage = productService.searchByName(keyword, PageRequest.of(page, size));
        } else {
            productPage = productService.findAll(PageRequest.of(page, size));
        }
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
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
        return "admin/manage-category/category_list";
    }
    @GetMapping("/profile")
    public String viewAdminProfile(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !currentUser.getRole().getRoleName().equals("ADMIN")) {
            return "redirect:/login";
        }
        model.addAttribute("admin", currentUser);
        return "admin/profile";
    }



}
