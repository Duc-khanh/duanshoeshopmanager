package com.codegym.shoeshopmanager.controller;

import com.codegym.shoeshopmanager.model.CartItem;
import com.codegym.shoeshopmanager.model.User;
import com.codegym.shoeshopmanager.service.CartService;
import com.codegym.shoeshopmanager.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @PostMapping("/order")
    public String checkout(
            HttpSession session,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "selectedItems", required = false) List<Integer> selectedItemIds

    ) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }
//        if (selectedItemIds == null || selectedItemIds.isEmpty()) {
//            List<CartItem> allCartItems = cartService.getCartItems(user);
//            selectedItemIds = allCartItems.stream().map(CartItem::getItemID).collect(Collectors.toList());
//        }
//
//        if (selectedItemIds == null || selectedItemIds.isEmpty()) {
//            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn sản phẩm để thanh toán!");
//            return "redirect:/cart";
//        }

        try {
            List<CartItem> selectedItems = orderService.getCartItemsByIds(selectedItemIds);

            orderService.placeOrder(user, selectedItems);

            cartService.removeCartItemsByIds(user, selectedItemIds);

            int newCount = cartService.getCartItemCount(user);
            session.setAttribute("cartItemCount", newCount);

            redirectAttributes.addFlashAttribute("success", "Thanh toán thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi thanh toán: " + e.getMessage());
        }

        return "redirect:/cart";
    }
}
