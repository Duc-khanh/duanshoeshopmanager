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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "productID"));

        if (keyword != null && !keyword.isEmpty()) {
            productPage = productService.searchByName(keyword, pageRequest);
        } else {
            productPage = productService.findAll(pageRequest);
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
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
    @GetMapping("/account/info")
    public String viewAccountInfo(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "admin/manage-account-info/account-info";
    }

    @GetMapping("/account/edit")
    public String editAccountForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "admin/manage-account-info/edit-account";
    }

    @PostMapping("/account/edit")
    public String updateAccount(@ModelAttribute("user") User updatedUser,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        currentUser.setEmail(updatedUser.getEmail());
        currentUser.setAddress(updatedUser.getAddress());

        // Xử lý upload ảnh
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());

                // Đường dẫn thư mục upload (nên khai báo ở config hoặc biến static)
                String uploadDir = "src/main/resources/static/image/";

                // Tạo thư mục nếu chưa có
                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) {
                    uploadPath.mkdirs();
                }

                // Đường dẫn đầy đủ để lưu file
                Path filePath = Paths.get(uploadDir + fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Lưu đường dẫn ảnh vào DB (đường dẫn web)
                currentUser.setImage("/image/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "Lỗi khi lưu ảnh!");
            }
        }

        userService.save(currentUser);
        session.setAttribute("currentUser", currentUser);

        redirectAttributes.addFlashAttribute("message", "Cập nhật thành công!");
        return "redirect:/admin/account/info";
    }


}
