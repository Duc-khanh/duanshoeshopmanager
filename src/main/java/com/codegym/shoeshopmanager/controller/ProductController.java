package com.codegym.shoeshopmanager.controller;

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
import java.util.List;

@Controller
@RequestMapping("/managerProduct")
public class ProductController {
    @Autowired
    private IProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Value("${file-upload}")
    private String uploadDir;



    @GetMapping("/create")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/manageProduct/add_product";
    }

    @GetMapping("/edit/{productID}")
    public String showEditForm(@PathVariable Integer productID , Model model) {

        Product product = productService.findById(productID);
        System.out.println(product);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.findAll());
        return "admin/manageProduct/update_product";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product) throws IOException {
        MultipartFile imageFile = product.getImageFile();

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());

            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            Path filePath = Paths.get(uploadDir + fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            product.setImage("/image/" + fileName);
        }

        productService.save(product);
        return "redirect:/admin/products";
    }
    @GetMapping("/delete/{productID}")
    public String deleteProduct(@PathVariable Integer productID) {
        productService.delete(productID);
        return "redirect:/admin/products";
    }
    @GetMapping("/view/{productID}")
    public String showViewForm(@PathVariable Integer productID , Model model) {
        Product product = productService.findById(productID);
        model.addAttribute("product", product);
        return "admin/manageProduct/view_product";
    }
    @GetMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model) {
        List<Product> products = productService.searchByName(keyword);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "admin/manageProduct/product_list";
    }

}
