package com.codegym.shoeshopmanager.controller;

import com.codegym.shoeshopmanager.model.Category;
import com.codegym.shoeshopmanager.model.Product;
import com.codegym.shoeshopmanager.service.CategoryService;
import com.codegym.shoeshopmanager.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Value("${file-upload}")
    private String uploadDir;

    @GetMapping("/create")
    public String showCreateCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/manage-category/add_category";
    }

//    @PostMapping("/save")
//    public String saveCategory(@ModelAttribute Category category,
//                               @RequestParam("imageFile") MultipartFile imageFile) {
//        try {
//            if (!imageFile.isEmpty()) {
//                String fileName = imageFile.getOriginalFilename();
//                File uploadPath = new File(uploadDir);
//                if (!uploadPath.exists()) uploadPath.mkdirs();
//                File dest = new File(uploadPath, fileName);
//                Files.copy(imageFile.getInputStream(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                category.setImage("/images/" + fileName);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        categoryService.save(category);
//        return "redirect:/admin/categories";
//    }

    @GetMapping("/edit/{id}")
    public String showEditCategory(Model model, @PathVariable Integer id) {
        Category category = categoryService.findById(id);
        model.addAttribute("category", category);
        return "admin/manage-category/update_category";
    }

    @PostMapping("/save")
    public String saveProduct(
            @ModelAttribute Category category,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam(value = "oldImagePath", required = false) String oldImagePath
    ) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }
            Path filePath = Paths.get(uploadDir + fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            category.setImage("/image/" + fileName);
        } else {

            category.setImage(oldImagePath);
        }
        categoryService.save(category);
        return "redirect:/admin/categories";
    }



    @GetMapping("/delete/{id}")
    public String deleteCategory( @PathVariable Integer id) {
        categoryService.delete(id);
        return "redirect:/admin/categories";
    }
}  