package com.codegym.shoeshopmanager.controller;

import com.codegym.shoeshopmanager.model.Product;
import com.codegym.shoeshopmanager.model.User;
import com.codegym.shoeshopmanager.service.CategoryService;
import com.codegym.shoeshopmanager.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private IProductService productService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !currentUser.getRole().getRoleName().equals("ADMIN")) {
            return "redirect:/login";
        }
        model.addAttribute("admin", currentUser);
        return "admin/dashboard";
    }
    @GetMapping("/products")
    public String listProducts(Model model, @RequestParam(required = false) String keyword) {
        List<Product> products = (keyword != null)
                ? productService.searchByName(keyword)
                : productService.findAll();
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "admin/product_list";
    }
    @GetMapping("products/create")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/add_product";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product,
                              @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            // Lấy tên file gốc
            String fileName = imageFile.getOriginalFilename();

            // Đảm bảo thư mục tồn tại
            String uploadDir = "src/main/resources/static/images/";
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            // Tạo đường dẫn đến file đích
            Path filePath = Paths.get(uploadDir, fileName);

            // Ghi file
            Files.write(filePath, imageFile.getBytes());

            // Gán tên file vào trường image của Product
            product.setImage(fileName);
        }

        // Lưu sản phẩm
        productService.save(product);

        return "redirect:/admin/products";
    }




}
