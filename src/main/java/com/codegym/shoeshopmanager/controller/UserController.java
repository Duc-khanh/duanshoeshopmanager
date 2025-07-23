package com.codegym.shoeshopmanager.controller;

import com.codegym.shoeshopmanager.model.Category;
import com.codegym.shoeshopmanager.model.Product;
import com.codegym.shoeshopmanager.service.CategoryService;
import com.codegym.shoeshopmanager.service.ICategoryService;
import com.codegym.shoeshopmanager.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/managerUser")
public class UserController {
    @Autowired
    private IProductService productService;


    @GetMapping("/searchProduct")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model) {
        List<Product> products = productService.searchByName(keyword)
                .stream()
                .filter(p -> p.getStock() > 0)
                .collect(Collectors.toList());

        List<Product> featuredProducts = new ArrayList<>(products);
        Collections.shuffle(featuredProducts);
        featuredProducts = featuredProducts.stream()
                .limit(10)
                .collect(Collectors.toList());

        List<Product> discountedProducts = products.stream()
                .sorted(Comparator.comparingDouble(Product::getPrice))
                .limit(10)
                .collect(Collectors.toList());

        model.addAttribute("featuredProducts", featuredProducts);
        model.addAttribute("discountedProducts", discountedProducts);
        model.addAttribute("keyword", keyword);

        return "users/homeUser/home";
    }

    @GetMapping("/view/{productID}")
    public String showViewForm(@PathVariable Integer productID, Model model) {
        Product product = productService.findById(productID);
        model.addAttribute("product", product);
        return "users/homeUser/view_product";
    }


}
