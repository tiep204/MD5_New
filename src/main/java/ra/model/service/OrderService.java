package ra.model.service;

import ra.model.entity.Order;
import ra.payload.response.OrderResponse;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrder();
    Order save(Order order);
    Order findByID(int orderID);
    List<Order> findByUser(int userId);
    List<OrderResponse> adminGetAllOrder();
}
