package com.codegym.shoeshopmanager.controller;

import com.codegym.shoeshopmanager.model.CartItem;
import com.codegym.shoeshopmanager.model.Order;
import com.codegym.shoeshopmanager.model.OrderStatus;
import com.codegym.shoeshopmanager.model.User;
import com.codegym.shoeshopmanager.repository.OrderRepository;
import com.codegym.shoeshopmanager.service.CartService;
import com.codegym.shoeshopmanager.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/order")
    public String checkout(
            HttpSession session,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "selectedItems", required = false) List<Integer> selectedProductIds
    ) {
        if (selectedProductIds == null || selectedProductIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn sản phẩm để thanh toán!");
            return "redirect:/cart";
        }

        session.setAttribute("pendingCheckoutItems", selectedProductIds);

        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
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

        for (CartItem item : selectedItems) {
            int requestedQty = item.getQuantity();
            int availableStock = item.getProduct().getStock();
            if (requestedQty > availableStock) {
                redirectAttributes.addFlashAttribute("error",
                        "Sản phẩm '" + item.getProduct().getProductName() + "' chỉ còn " + availableStock + " sản phẩm.");
                return "redirect:/cart";
            }
        }
        try {
            Order newOrder = orderService.placeOrder(user, selectedItems);
            cart.removeIf(item -> selectedProductIds.contains(item.getProduct().getProductID()));
            session.setAttribute("cart", cart);
            session.setAttribute("cartItemCount", getTotalQuantity(cart));

            session.removeAttribute("pendingCheckoutItems");
            redirectAttributes.addFlashAttribute("success", "Thanh toán thành công!");
            redirectAttributes.addAttribute("orderID", newOrder.getOrderID());

            return "redirect:/order/detail/{orderID}";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi thanh toán: " + e.getMessage());
            return "redirect:/cart";
        }
    }
    @GetMapping("/order/detail/{orderID}")
    public String orderDetail(@PathVariable("orderID") Integer id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }
        Optional<Order> orderOptional = orderRepository.findOrderWithDetails(id);

        if (!orderOptional.isPresent() || !orderOptional.get().getUser().getUserID().equals(user.getUserID())) {
            return "redirect:/users";
        }
        model.addAttribute("order", orderOptional.get());
        return "users/cart/order-detail";
    }
    @GetMapping("/orders")
    public String listOrders(Model model) {
        List<Order> orders = orderService.findAll();
        model.addAttribute("orders", orders);
        return "admin/manage-order/list_orders";
    }
@GetMapping("/view/{id}")
public String viewOrder(@PathVariable Integer id, Model model) {
    Order order = orderService.findOrderWithDetails(id);
    if (order == null) return "redirect:/admin/orders";
    double totalAmount = order.getOrderDetails().stream()
            .mapToDouble(d -> d.getPrice() * d.getQuantity())
            .sum();
    model.addAttribute("order", order);
    model.addAttribute("totalAmount", totalAmount);
    return "admin/manage-order/view_order";
}
    @PostMapping("/update-status")
    public String updateStatus(@RequestParam Integer orderId, @RequestParam String status) {
        orderService.updateStatus(orderId, status);
        return "redirect:/orders";
    }
//    @GetMapping("/my-orders")
//    public String viewMyOrders(HttpSession session, Model model) {
//        User user = (User) session.getAttribute("currentUser");
//        if (user == null) {
//            return "redirect:/login";
//        }
//        List<Order> myOrders = orderService.getOrdersByUser(user);
//        model.addAttribute("orders", myOrders);
//        return "users/cart/my-orders";
//    }
@GetMapping("/my-orders")
public String viewMyOrders(@RequestParam(value = "status", required = false) String status,
                           HttpSession session,
                           Model model) {
    User user = (User) session.getAttribute("currentUser");
    if (user == null) {
        return "redirect:/login";
    }
    List<Order> myOrders;
    if (status != null && !status.isEmpty()) {
        myOrders = orderService.getOrdersByUserAndStatus(user, status);
    } else {
        myOrders = orderService.getOrdersByUser(user);
    }
    model.addAttribute("orders", myOrders);
    model.addAttribute("selectedStatus", status);
    model.addAttribute("statuses", OrderStatus.values());

    return "users/cart/my-orders";
}


    private int getTotalQuantity(List<CartItem> cart) {
        return cart.stream().mapToInt(CartItem::getQuantity).sum();
    }
}