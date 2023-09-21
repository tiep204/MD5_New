package ra.model.service;

import ra.model.entity.Order;
import ra.model.entity.Users;
import ra.payload.response.OrderResponse;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrder();
    Order save(Order order);
    Order findByID(int orderID);
    List<Order> findByUser(int userId);
    List<OrderResponse> adminGetAllOrder();
    List<OrderResponse> adminGetAllAccepted();
    List<OrderResponse> adminGetAllDelivery();
    List<OrderResponse> adminGetAllOK();
    List<OrderResponse> adminGetAllCancel();
    List<OrderResponse> adminGetAllUserCancel();
    List<OrderResponse> adminGetAllPending();
}