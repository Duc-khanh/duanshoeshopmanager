package com.codegym.shoeshopmanager.controller;

import com.codegym.shoeshopmanager.model.Category;
import com.codegym.shoeshopmanager.model.Product;
import com.codegym.shoeshopmanager.model.User;
import com.codegym.shoeshopmanager.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class HomeUserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IFavoriteService favoriteService;
    @Autowired
    private IProductService productService;
    @Value("${file-upload}")
    private String uploadDir;


    @GetMapping("")
    public String dashboard(HttpSession session, Model model) {
        List<Product> allProducts = productService.findAll();

        List<Product> featuredProducts = new ArrayList<>(allProducts);
        Collections.shuffle(featuredProducts);
        featuredProducts = featuredProducts.stream()
                .filter(p -> p.getStock() > 0)
                .limit(10)
                .collect(Collectors.toList());

        List<Product> discountedProducts = allProducts.stream()
                .filter(p -> p.getStock() > 0)
                .sorted(Comparator.comparingDouble(Product::getPrice))
                .limit(10)
                .collect(Collectors.toList());

        model.addAttribute("featuredProducts", featuredProducts);
        model.addAttribute("discountedProducts", discountedProducts);

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            model.addAttribute("user", currentUser);
        }

        return "users/homeUser/home";
    }
    @GetMapping("/create")
    public String showFormNewUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.findAll());
        return "admin/manageUser/add_user";
    }
    @GetMapping("edit/{id}")
    public String showEditFormUser(@PathVariable Integer id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.findAll());
        return "admin/manageUser/update_user";
    }

    @PostMapping("/save")
    public String saveUser(
            @ModelAttribute User user,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam(value = "oldImagePath", required = false) String oldImagePath
    ) throws IOException {
        User existingUser = userService.findById(user.getUserID());
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }
            Path filePath = Paths.get(uploadDir + fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            user.setImage("/image/" + fileName);
        } else {
            user.setImage(oldImagePath);
        }

        userService.save(user);
        return "redirect:/admin/user";
    }

    @PostMapping("/add")
    public String createUser(@ModelAttribute("user") User user) {
        MultipartFile imageFile = user.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = imageFile.getOriginalFilename();
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                imageFile.transferTo(filePath.toFile());

                user.setImage("/image/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        userService.save(user);
        return "redirect:/admin/user";
    }

    @GetMapping("view/{id}")
    public String showViewFormUser(@PathVariable Integer id, Model model) {
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
    @GetMapping("/home")
    public String showHomePage(Model model) {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "users/layout";
    }
    @ModelAttribute("categories")
    public List<Category> getCategories() {
        return categoryService.findAll();
    }
@PostMapping("/favorite/add/{productId}")
public String addFavorite(@PathVariable("productId") Integer productId,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
    User currentUser = (User) session.getAttribute("currentUser");

    if (currentUser == null) {
        redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để sử dụng tính năng này!");
        return "redirect:/login";
    }

    Product product = productService.findById(productId);
    if (product != null) {
        favoriteService.addFavorite(currentUser, product);
        int favoriteCount = favoriteService.countFavoritesByUser(currentUser);
        session.setAttribute("favoriteCount", favoriteCount);
    }
    return "redirect:/users";
}
    @PostMapping("/favorite/remove/{productId}")
    public String removeFavorite(@PathVariable("productId") Integer productId, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            Product product = productService.findById(productId);
            favoriteService.removeFavorite(currentUser, product);
            List<Product> favorites = favoriteService.getFavoriteProducts(currentUser);
            session.setAttribute("favoriteCount", favorites.size());
        }
        return "redirect:/users/favorites";
    }
    @GetMapping("/favorites")
    public String viewFavorites(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            List<Product> favorites = favoriteService.getFavoriteProducts(currentUser);
            model.addAttribute("favoriteProducts", favorites);
        }
        return "users/cart/favorite-list";
    }
    @ModelAttribute
    public void populateFavoriteCount(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            List<Product> favorites = favoriteService.getFavoriteProducts(currentUser);
            session.setAttribute("favoriteCount", favorites.size());
        }
    }
    @GetMapping("/account")
    public String viewAccount(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", currentUser);
        return "users/homeUser/account";
    }
@GetMapping("/user/edit")
public String showEditForm(Model model, HttpSession session) {
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        return "redirect:/login";
    }
    model.addAttribute("user", currentUser);
    return "users/user/edit-user";
}

    @PostMapping("/user/update")
    public String updateUser(@ModelAttribute("user") User user, HttpSession session) {
        userService.update(user);
        session.setAttribute("currentUser", userService.findById(user.getUserID()));
        return "redirect:/users/account";
    }




}
