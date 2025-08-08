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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String showEditForm(@PathVariable Integer productID, Model model) {

        Product product = productService.findById(productID);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.findAll());
        return "admin/manageProduct/update_product";
    }

    @PostMapping("/products/save")
    public String saveProduct(
            @ModelAttribute Product product,
            RedirectAttributes redirectAttributes,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam(value = "oldImagePath", required = false) String oldImagePath,
            Model model
    ) throws IOException {

        boolean isNew = (product.getProductID() == null);

        if (isNew && productService.existsByProductName(product.getProductName())) {
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("errorNameExists", true);
            return "admin/manageProduct/add_product";
        }

        if (!isNew) {
            Product existing = productService.findById(product.getProductID());
            if (!existing.getProductName().equals(product.getProductName()) &&
                    productService.existsByProductName(product.getProductName())) {
                product.setImage(existing.getImage());
                model.addAttribute("product", product);
                model.addAttribute("categories", categoryService.findAll());
                model.addAttribute("errorNameExists", true);
                return "admin/manageProduct/update_product";
            }
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }
            Path filePath = Paths.get(uploadDir + fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            product.setImage("/image/" + fileName);
        } else {
            product.setImage(oldImagePath);
        }

        productService.save(product);
        redirectAttributes.addFlashAttribute("successMessage", isNew ? "Thêm sản phẩm thành công!" : "Cập nhật sản phẩm thành công!");
        return "redirect:/admin/products";
    }
    @GetMapping("/delete/{productID}")
    public String markOutOfStock(@PathVariable Integer productID , RedirectAttributes redirectAttributes) {
        productService.markAsOutOfStock(productID);
        redirectAttributes.addFlashAttribute("successMessage", "Đã chuyển trạng thái sản phẩm là hết hàng.");

        return "redirect:/admin/products";
    }


    @GetMapping("/view/{productID}")
    public String showViewForm(@PathVariable Integer productID, Model model) {
        Product product = productService.findById(productID);
        model.addAttribute("product", product);
        return "admin/manageProduct/view_product";
    }

    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Model model) {
        List<Product> products = productService.searchByName(keyword);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "admin/manageProduct/product_list";
    }


    @GetMapping("/category/{id}")
    public String showProductsByCategory(@PathVariable Integer id, Model model) {
        List<Product> products = productService.findByCategoryId(id);
        Category category = categoryService.findById(id);
        List<Category> categories = categoryService.findAll();

        model.addAttribute("products", products);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("categories", categories);

        return "users/homeUser/product-by-category";
    }




}
