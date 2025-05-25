package com.codegym.shoeshopmanager.controller;

import com.codegym.shoeshopmanager.model.Category;
import com.codegym.shoeshopmanager.service.CategoryService;
import com.codegym.shoeshopmanager.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/create")
    public String showCreateCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/manageCategory/add_category";
    }

    @PostMapping("/save")
    public String saveCategory(@ModelAttribute Category category) {
        categoryService.save(category);
        return "redirect:/admin/categories";
    }


    @GetMapping("/edit/{id}")
    public String showEditCategory(Model model, @PathVariable Integer id) {
        Category category = categoryService.findById(id);
        model.addAttribute("category", category);
        return "admin/manageCategory/update_category";
    }
}
