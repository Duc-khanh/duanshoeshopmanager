package com.codegym.shoeshopmanager.controller;

import com.codegym.shoeshopmanager.model.CartItem;
import com.codegym.shoeshopmanager.model.User;
import com.codegym.shoeshopmanager.service.CartService;
import com.codegym.shoeshopmanager.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/order")
    public String checkout(
            HttpSession session,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "selectedItems", required = false) List<Integer> selectedProductIds
    ) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        if (selectedProductIds == null || selectedProductIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn sản phẩm để thanh toán!");
            return "redirect:/cart";
        }

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống.");
            return "redirect:/cart";
        }

        List<CartItem> selectedItems = cart.stream()
                .filter(item -> selectedProductIds.contains(item.getProduct().getProductID()))
                .collect(Collectors.toList());
        if (selectedItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm đã chọn trong giỏ hàng.");
            return "redirect:/cart";
        }
        try {
            orderService.placeOrder(user, selectedItems);
            List<CartItem> updatedCart = new ArrayList<>();
            for (CartItem item : cart) {
                Integer id = item.getProduct().getProductID();
                if (!selectedProductIds.contains(id)) {
                    updatedCart.add(item);
                }
            }

//            cart.removeIf(item -> selectedProductIds.contains(item.getProduct().getProductID()));
//            session.setAttribute("cart", cart);
//            session.setAttribute("cartItemCount", getTotalQuantity(cart));


            redirectAttributes.addFlashAttribute("success", "Thanh toán thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi thanh toán: " + e.getMessage());
        }
        return "redirect:/cart";
    }

    private int getTotalQuantity(List<CartItem> cart) {
        return cart.stream().mapToInt(CartItem::getQuantity).sum();
    }


}

