package ra.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ra.model.entity.Order;
import ra.model.entity.Users;
import ra.model.repository.UserRepository;
import ra.model.service.CartService;
import ra.model.service.OrderDetailsService;
import ra.model.service.OrderService;
import ra.model.service.UserService;
import ra.model.service.serviceImp.MailService;
import ra.payload.response.MessageResponse;
import ra.payload.response.OrderResponse;
import ra.security.CustomUserDetails;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/order")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    CartService cartService;
    @Autowired
    UserService userService;
    @Autowired
    OrderDetailsService orderDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MailService mailService;


    ///Admin duyet đơn hàng
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{orderID}")
    public ResponseEntity<?> updateOrderStatusToAdmit(@PathVariable("orderID") int orderID) {
        try {
            Order order = orderService.findByID(orderID);
            order.setOrderStatus("Accepted");
            orderService.save(order);
            return ResponseEntity.ok(new MessageResponse("Order has been accepted"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageResponse("Có lỗi trong quá trình xử lý vui lòng thử lại!"));
        }
    }


    //Đơn hàng đang được giao
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delivery/{orderID}")
    public ResponseEntity<?> updateOrderStatusToDelivery(@PathVariable("orderID") int orderID) {
        try {
            Order order = orderService.findByID(orderID);
            order.setOrderStatus("Delivery");
            orderService.save(order);
            return ResponseEntity.ok(new MessageResponse("Order is being delivery"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageResponse("Có lỗi trong quá trình xử lý vui lòng thử lại!"));
        }
    }

    ////admin xác nhận đơn hàng đã được giao
    @GetMapping("/finish/{orderID}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatusToFinish(@PathVariable("orderID") int orderID) {
        try {
            Order order = orderService.findByID(orderID);
            order.setOrderStatus("OK");
            orderService.save(order);
            return ResponseEntity.ok(new MessageResponse("Order has been paid"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageResponse("Có lỗi trong quá trình xử lý vui lòng thử lại!"));
        }
    }

    ///Admin hủy đơn hàng
    @GetMapping("/cancel/{orderID}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cancel(@PathVariable("orderID") int orderID) {
        try {
            Order order = orderService.findByID(orderID);
            order.setOrderStatus("cancel");
            orderService.save(order);
            mailService.sendMail(order.getUsers().getEmail(), "Cancel", "Đơn hàng của bạn đã bị hủy");
            return ResponseEntity.ok(new MessageResponse("Đơn hàng đã bị hủy"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageResponse("Có lỗi trong quá trình xử lý vui lòng thử lại!"));
        }
    }


    //User Hủy đơn hàng
    @GetMapping("/UserCancel/{orderID}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> userCancelOrder(@PathVariable("orderID") int orderID) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users users = userRepository.findById(userDetails.getUserId()).get();
        try {
            Order order = orderService.findByID(orderID);
            if (users.getUserId() == order.getUsers().getUserId()) {
                // Nêú mà đơn hàng đang chờ xác nhận thì người dùng có thể hủy đơn hàng
                if (order.getOrderStatus().equals("Pending")) {
                    order.setOrderStatus("UserCancel");
                    orderService.save(order);
                    mailService.sendMail(order.getUsers().getEmail(), "Cancel", "bạn đã hủy Đơn hàng của bạn");
                } else {
                    mailService.sendMail(order.getUsers().getEmail(), "Cancel", "bạn khong thể hủy đơn hàng");
                    return ResponseEntity.ok(new MessageResponse("Đơn hàng nay khong the huy"));
                }
            }
            return ResponseEntity.ok(new MessageResponse("Đơn hàng đã bị hủy"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageResponse("Có lỗi trong quá trình xử lý vui lòng thử lại!"));
        }
    }

/*    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponse> getAllOrder() {
        List<OrderResponse> listOrder = new ArrayList<>();
        List<Order> list = orderService.getAllOrder();
        for (Order order : list) {
            if (!Objects.equals(order.getOrderStatus(), "OK")) {
                OrderResponse orderResponse = new OrderResponse();
                orderResponse.setOrderID(order.getOrderID());
                orderResponse.setOrderStatus(order.getOrderStatus());
                orderResponse.setTotalAmount(order.getTotalAmount());
                orderResponse.setCreated(order.getCreated());
                orderResponse.setUsersId(order.getUsers().getUserId());
                listOrder.add(orderResponse);
            }
        }
        return listOrder;
    }*/

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrder() {
        List<OrderResponse> orderResponseList = orderService.adminGetAllOrder();
        return new ResponseEntity<>(orderResponseList, HttpStatus.OK);
    }

    /*    @GetMapping("/userOrder")
        @PreAuthorize("hasRole('USER')")
        public OrderResponse getOrderByUser() {
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Order order = orderService.findByUser(userDetails.getUserId());
            OrderResponse orderResponse = new OrderResponse();
            if (!Objects.equals(order.getOrderStatus(), "OK")) {
                orderResponse.setOrderID(order.getOrderID());
                orderResponse.setOrderStatus(order.getOrderStatus());
                orderResponse.setTotalAmount(order.getTotalAmount());
                orderResponse.setCreated(order.getCreated());
                orderResponse.setUsersId(order.getUsers().getUserId());
            }
            return orderResponse;
        }  */
    @GetMapping("/userOrder")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getOrderByUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users users = userRepository.findById(userDetails.getUserId()).get();
        List<Order> list = orderService.findByUser(users.getUserId());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /*    @PostMapping
        public ResponseEntity<?> saveOrder() {
            try {
                Order order = new Order();
                CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                List<Cart> listCart = cartService.findAllUserCartById(userDetails.getUserId());
                List<OrderDetails> listOrderDetails = new ArrayList<>();
                float totalAmount = 0f;
                for (Cart cart : listCart) {
                    totalAmount += cart.getTotalPrice();
                }
                order.setOrderStatus("Pending");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date dateNow = new Date();
                String strNow = sdf.format(dateNow);
                try {
                    order.setCreated(sdf.parse(strNow));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                order.setUsers(userService.getUserByID(userDetails.getUserId()));
                order.setTotalAmount(totalAmount);
                orderService.save(order);
                for (Cart cart : listCart) {
                    OrderDetails orderDetails = new OrderDetails();
                    orderDetails.setOrder(order);
                    orderDetails.setQuantity(cart.getQuantity());
                    orderDetails.setProduct(cart.getProduct());
                    orderDetails.setPrice(cart.getPrice());
                    orderDetails.setTotal(cart.getProduct().getPrice() * cart.getQuantity());
                    orderDetailsService.save(orderDetails);
                    listOrderDetails.add(orderDetails);
                }
                for (Cart cart : listCart) {
                    cartService.delete(cart.getCartID());
                }
                return ResponseEntity.ok(new MessageResponse("Order successfully"));
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().body(new MessageResponse("Có lỗi trong quá trình xử lý vui lòng thử lại!"));
            }
        }*/
    @PostMapping
    public ResponseEntity<?> saveOrder() {
        try {
            orderDetailsService.saveOrder();
            return ResponseEntity.ok(new MessageResponse("Đặt hàng success"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageResponse("Có lỗi trong quá trình xử lý vui lòng thử lại!"));
        }
    }

    //    @GetMapping("/{orderID}")
//    public ResponseEntity<?> getAllOrderDetails(@PathVariable("orderID") int orderID) {
//        List<OrderDetails> listOrderDetails = orderDetailsService.getAllOrderDetails(orderID);
//        List<OrderDetailsResponse> list = new ArrayList<>();
//        for (OrderDetails orderDetails : listOrderDetails) {
//            OrderDetailsResponse orderDetailsResponse = new OrderDetailsResponse();
//            orderDetailsResponse.setQuantity(orderDetails.getQuantity());
//            orderDetailsResponse.setPrice(orderDetails.getProduct().getPrice());
//            orderDetailsResponse.setTotal(orderDetails.getTotal());
//            orderDetailsResponse.setProductName(orderDetails.getProduct().getProductName());
//            list.add(orderDetailsResponse);
//        }
//        return ResponseEntity.ok(list);
//    }
    @GetMapping("/{orderID}")
    public ResponseEntity<?> getAllOrderDetails(@PathVariable("orderID") int orderID) {
        return new ResponseEntity<>(orderDetailsService.getAllOrderDetail(orderID), HttpStatus.OK);
    }

    // nhung don hang chua duoc duyet
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllPending() {
        List<OrderResponse> orderResponseList = orderService.adminGetAllPending();
        return new ResponseEntity<>(orderResponseList, HttpStatus.OK);
    }

    //Nhung don hang da duoc duyet
    @GetMapping("/accepted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllAccepted() {
        List<OrderResponse> orderResponseList = orderService.adminGetAllAccepted();
        return new ResponseEntity<>(orderResponseList, HttpStatus.OK);
    }

    //nhung don hang dang van chuyen
    @GetMapping("/delivery")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllDelivery() {
        List<OrderResponse> orderResponseList = orderService.adminGetAllDelivery();
        return new ResponseEntity<>(orderResponseList, HttpStatus.OK);
    }

    //nhung don hang nguoi dung da nhan duoc
    @GetMapping("/OK")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOK() {
        List<OrderResponse> orderResponseList = orderService.adminGetAllOK();
        return new ResponseEntity<>(orderResponseList, HttpStatus.OK);
    }

    // nhung don hang da bi admin huy
    @GetMapping("/adminCancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllAdminCancel() {
        List<OrderResponse> orderResponseList = orderService.adminGetAllCancel();
        return new ResponseEntity<>(orderResponseList, HttpStatus.OK);
    }

    //nhung don hang da duoc nguoi dung huy
    @GetMapping("/userCancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllUserCancel() {
        List<OrderResponse> orderResponseList = orderService.adminGetAllUserCancel();
        return new ResponseEntity<>(orderResponseList, HttpStatus.OK);
    }
}