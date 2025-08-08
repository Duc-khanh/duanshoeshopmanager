package com.codegym.shoeshopmanager.controller;

import com.codegym.shoeshopmanager.model.*;
import com.codegym.shoeshopmanager.repository.OrderRepository;
import com.codegym.shoeshopmanager.service.CartService;
import com.codegym.shoeshopmanager.service.IProductService;
import com.codegym.shoeshopmanager.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private IProductService productService;

    @PostMapping("/order")
    public String checkout(
            HttpSession session,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "selectedItems", required = false) List<Integer> selectedProductIds,
            @RequestParam Map<String, String> quantities
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
            String key = "quantities[" + item.getProduct().getProductID() + "]";
            if (quantities.containsKey(key)) {
                try {
                    int newQty = Integer.parseInt(quantities.get(key));
                    if (newQty > 0) {
                        item.setQuantity(newQty);
                    }
                } catch (NumberFormatException e) {
                }
            }
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

            for (CartItem item : selectedItems) {
                Product product = item.getProduct();
                int newStock = product.getStock() - item.getQuantity();
                product.setStock(newStock);
                productService.save(product);
            }

            session.removeAttribute("pendingCheckoutItems");
            redirectAttributes.addAttribute("orderID", newOrder.getOrderID());

            return "redirect:/order/detail/{orderID}";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi đặt hàng: " + e.getMessage());
            return "redirect:/cart";
        }
    }



    @GetMapping("/order/details/{orderID}")
    public String orderDetails(@PathVariable("orderID") Integer id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Order> orderOptional = orderRepository.findOrderWithDetails(id);
        if (!orderOptional.isPresent() || !orderOptional.get().getUser().getUserID().equals(user.getUserID())) {
            return "redirect:/users";
        }

        model.addAttribute("order", orderOptional.get());
        return "users/cart/order-details";
    }

    @GetMapping("/order/detail/{orderID}")
    public String orderSummary(@PathVariable("orderID") Integer id, Model model, HttpSession session) {
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
    public String updateStatus(@RequestParam Integer orderId,
                               @RequestParam String status,
                               RedirectAttributes redirectAttributes) {

        Order order = orderService.findOrderWithDetails(orderId);
        if (order == null) {
            redirectAttributes.addFlashAttribute("updateError", "Không tìm thấy đơn hàng.");
            return "redirect:/view/" + orderId;
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            redirectAttributes.addFlashAttribute("updateError", "Đơn hàng đã bị hủy, không thể cập nhật trạng thái.");
            return "redirect:/view/" + orderId;
        }

        orderService.updateStatus(order, status);

        redirectAttributes.addFlashAttribute("updateSuccess", true);
        return "redirect:/view/" + orderId;
    }


    @GetMapping("/my-orders")
    public String viewMyOrders(@RequestParam(value = "status", required = false) String status,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size,
                               HttpSession session,
                               Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderID"));

        Page<Order> orderPage;

        if (status != null && !status.isEmpty()) {
            orderPage = orderService.getOrdersByUserAndStatus(user, status, pageable);
        } else {
            orderPage = orderService.getOrdersByUser(user, pageable);
        }

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", OrderStatus.values());

        return "users/cart/my-orders";
    }

    @PostMapping("/order/cancel/{userID}")
    public String cancelOrder(@PathVariable("userID") Integer userID, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để thực hiện thao tác này.");
            return "redirect:/login";
        }

        Order order = orderService.findById(userID);
        if (order == null || !order.getUser().getUserID().equals(user.getUserID())) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng hoặc bạn không có quyền.");
            return "redirect:/my-orders";
        }

        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            redirectAttributes.addFlashAttribute("error", "Chỉ có thể huỷ đơn hàng khi đang chờ xác nhận.");
            return "redirect:/my-orders";
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderService.save(order);

//        redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được huỷ thành công.");
        redirectAttributes.addFlashAttribute("cancelSuccess", true);

        return "redirect:/my-orders";
    }


    private int getTotalQuantity(List<CartItem> cart) {
        return cart.stream().mapToInt(CartItem::getQuantity).sum();
    }

    @GetMapping("/order/confirm/{orderID}")
    public String confirmOrder(@PathVariable("orderID") Integer orderID, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        Optional<Order> orderOpt = orderRepository.findOrderWithDetails(orderID);

        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();

            if (!order.getUser().getUserID().equals(user.getUserID())) {
                return "redirect:/users";
            }

            order.setStatus(OrderStatus.PENDING);
            orderRepository.save(order);

            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart != null) {
                List<Integer> orderedProductIds = order.getOrderDetails().stream()
                        .map(od -> od.getProduct().getProductID())
                        .collect(Collectors.toList());

                cart.removeIf(item -> orderedProductIds.contains(item.getProduct().getProductID()));
                session.setAttribute("cart", cart);
                session.setAttribute("cartItemCount", getTotalQuantity(cart));
            }

            redirectAttributes.addFlashAttribute("success", "Đặt hàng thành công!");
            redirectAttributes.addFlashAttribute("addToCartSuccess", true);


        }

        return "redirect:/my-orders";
    }


    @PostMapping("/checkout/buy-now/{productID}")
    public String buyNow(@PathVariable Integer productID,
                         @RequestParam int quantity,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("loginRequired", true);
            return "redirect:/login";
        }

        Product product = productService.findById(productID);
        if (product == null) {
            redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại.");
            return "redirect:/product/" + productID;
        }

        if (quantity <= 0) {
            redirectAttributes.addFlashAttribute("error", "Số lượng phải lớn hơn 0.");
            return "redirect:/product/" + productID;
        }

        if (quantity > product.getStock()) {
            redirectAttributes.addFlashAttribute("error", "Không thể mua " + quantity + " sản phẩm. Chỉ còn " + product.getStock() + " chiếc trong kho.");
            return "redirect:/product/" + productID;
        }

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);

        List<CartItem> buyNowItems = new ArrayList<>();
        buyNowItems.add(cartItem);

        try {
            Order newOrder = orderService.placeOrder(user, buyNowItems);

            product.setStock(product.getStock() - quantity);
            productService.save(product);

            redirectAttributes.addAttribute("orderID", newOrder.getOrderID());
            return "redirect:/order/detail/{orderID}";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể mua ngay: " + e.getMessage());
            return "redirect:/product/" + productID;
        }
    }


    @GetMapping("/orders")
    public String listOrders(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) String status,
                             Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderID"));
        Page<Order> orderPage;

        if (status != null && !status.isEmpty()) {
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status);
                orderPage = orderService.findByStatus(orderStatus, pageable);
            } catch (IllegalArgumentException e) {
                orderPage = Page.empty();
            }
        } else {
            orderPage = orderService.findAll(pageable);
        }

        model.addAttribute("orderPage", orderPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("selectedStatus", status);

        return "admin/manage-order/list_orders";
    }


}