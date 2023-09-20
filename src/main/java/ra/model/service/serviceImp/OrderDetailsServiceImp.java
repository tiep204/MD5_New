package ra.model.service.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ra.model.entity.Cart;
import ra.model.entity.Order;
import ra.model.entity.OrderDetails;
import ra.model.repository.OrderDetailsRepo;
import ra.model.service.CartService;
import ra.model.service.OrderDetailsService;
import ra.model.service.OrderService;
import ra.model.service.UserService;
import ra.payload.response.OrderDetailsResponse;
import ra.security.CustomUserDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
public class OrderDetailsServiceImp implements OrderDetailsService {
    @Autowired
    OrderDetailsRepo orderDetailsRepo;
    @Autowired
    CartService cartService;
    @Autowired
    UserService userService;
    @Autowired
    OrderService orderService;
    @Override
    public List<OrderDetails> getAllOrderDetails(int orderID) {
        return orderDetailsRepo.findByOrder_OrderID(orderID);
    }

    @Override
    public OrderDetails save(OrderDetails orderDetails) {
        return orderDetailsRepo.save(orderDetails);
    }

    @Override
    public void saveOrder() {
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
            save(orderDetails);
            listOrderDetails.add(orderDetails);
        }
        for (Cart cart : listCart) {
            cartService.delete(cart.getCartID());
        }
    }

    @Override
    public List<OrderDetailsResponse> getAllOrderDetail(int orderID) {
        List<OrderDetails> listOrderDetails = getAllOrderDetails(orderID);
        List<OrderDetailsResponse> list = new ArrayList<>();
        for (OrderDetails orderDetails : listOrderDetails) {
            OrderDetailsResponse orderDetailsResponse = new OrderDetailsResponse();
            orderDetailsResponse.setQuantity(orderDetails.getQuantity());
            orderDetailsResponse.setPrice(orderDetails.getProduct().getPrice());
            orderDetailsResponse.setTotal(orderDetails.getTotal());
            orderDetailsResponse.setProductName(orderDetails.getProduct().getProductName());
            list.add(orderDetailsResponse);
        }
        return list;
    }

}
