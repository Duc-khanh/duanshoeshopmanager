package com.codegym.shoeshopmanager.controller;

import com.codegym.shoeshopmanager.model.CartItem;
import com.codegym.shoeshopmanager.model.Product;
import com.codegym.shoeshopmanager.model.User;
import com.codegym.shoeshopmanager.repository.ProductRepository;
import com.codegym.shoeshopmanager.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/add/{productId}")
    public String addToCart(@PathVariable Integer productId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            HttpSession session) {

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }

        boolean found = false;
        for (CartItem item : cart) {
            if (item.getProduct().getProductID().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }

        if (!found) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.add(newItem);
        }

        session.setAttribute("cart", cart);
        session.setAttribute("cartItemCount", getTotalQuantity(cart));
        return "redirect:/users";
    }

    @GetMapping("")
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cartItemCount", 0);
        }

        model.addAttribute("cartItems", cart);
        session.setAttribute("cartItemCount", getTotalQuantity(cart));
        return "users/cart/cart_view";
    }

    @GetMapping("/remove/{productId}")
    public String removeItem(@PathVariable Integer productId, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null) {
            cart.removeIf(item -> item.getProduct().getProductID().equals(productId));
            session.setAttribute("cart", cart);
            session.setAttribute("cartItemCount", getTotalQuantity(cart));
        }
        return "redirect:/cart";
    }
    private int getTotalQuantity(List<CartItem> cart) {
        int totalQuantity = 0;
        for (CartItem item : cart) {
            totalQuantity += item.getQuantity();
        }
        return totalQuantity;
    }

}





