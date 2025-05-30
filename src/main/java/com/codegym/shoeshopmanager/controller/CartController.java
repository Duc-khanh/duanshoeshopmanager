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
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;

    @GetMapping("/add/{productId}")
    public String addToCart(@PathVariable Integer productId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        cartService.addToCart(user, product, quantity);
        int cartItemCount = cartService.getCartItems(user).size();
        session.setAttribute("cartItemCount", cartItemCount);
        return "redirect:/users";
    }

    @GetMapping("")
    public String viewCart(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        List<CartItem> items = cartService.getCartItems(user);
        model.addAttribute("cartItems", items);
        session.setAttribute("cartItemCount", items.size());
        return "users/cart/cart_view";
    }

    @GetMapping("/remove/{cartItemId}")
    public String removeItem(@PathVariable Integer cartItemId, HttpSession session) {
        cartService.removeItem(cartItemId);
        User user = (User) session.getAttribute("currentUser");
        if (user != null) {
            int cartItemCount = cartService.getCartItemCount(user);
            session.setAttribute("cartItemCount", cartItemCount);
        }
        return "redirect:/cart";
    }


}

