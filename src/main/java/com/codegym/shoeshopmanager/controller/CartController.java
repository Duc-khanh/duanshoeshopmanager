package com.codegym.shoeshopmanager.controller;

import com.codegym.shoeshopmanager.model.CartItem;
import com.codegym.shoeshopmanager.model.Category;
import com.codegym.shoeshopmanager.model.Product;
import com.codegym.shoeshopmanager.model.User;
import com.codegym.shoeshopmanager.repository.ProductRepository;
import com.codegym.shoeshopmanager.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Integer productId,
                            @RequestParam("quantity") Integer quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("loginRequired", true);
            return "redirect:/login";
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        int availableStock = product.getStock();

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }

        int currentQuantityInCart = cart.stream()
                .filter(item -> item.getProduct().getProductID().equals(productId))
                .mapToInt(CartItem::getQuantity)
                .sum();

        if (currentQuantityInCart + quantity > availableStock) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Số lượng trong giỏ đã đạt tối đa sản phẩm có trong kho.");
            return "redirect:/managerUser/view/" + productId;
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
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.add(newItem);
        }

        session.setAttribute("cart", cart);
        session.setAttribute("cartItemCount", getTotalQuantity(cart));
        redirectAttributes.addFlashAttribute("addToCartSuccess", true);

//        return "redirect:/managerUser/view/" + productId;
        return "redirect:/users";

    }



    @GetMapping("")
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.removeAttribute("cartItemCount");
        } else {
            int totalQuantity = getTotalQuantity(cart);
            if (totalQuantity > 0) {
                session.setAttribute("cartItemCount", totalQuantity);
            } else {
                session.removeAttribute("cartItemCount");
            }
        }

        model.addAttribute("cartItems", cart);
        return "users/cart/cart_view";
    }


    @GetMapping("/remove/{productId}")
    public String removeItem(@PathVariable Integer productId, HttpSession session, RedirectAttributes redirectAttributes) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null) {
            cart.removeIf(item -> item.getProduct().getProductID().equals(productId));
            session.setAttribute("cart", cart);

            int totalQuantity = getTotalQuantity(cart);
            if (totalQuantity > 0) {
                session.setAttribute("cartItemCount", totalQuantity);
            } else {
                session.removeAttribute("cartItemCount");
            }
        }
        redirectAttributes.addFlashAttribute("deleteSuccess", true);
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





