package ra.model.service.mapper;

import ra.model.entity.Order;
import ra.payload.request.OrderRequest;
import ra.payload.response.OrderResponse;

import java.util.Date;

public class OrderMapper implements IGenericMapper<Order, OrderRequest, OrderResponse>{
    @Override
    public Order toEntity(OrderRequest orderRequest) {
        return Order.builder()
                .orderID(orderRequest.getOrderID())
                .totalAmount(orderRequest.getTotalAmount())
                .orderStatus(orderRequest.getOrderStatus())
                .created(new Date())
                .users(orderRequest.getUsers())
                .listOrderDetails(orderRequest.getListOrderDetails())
                .build();
    }

    @Override
    public OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .orderID(order.getOrderID())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getOrderStatus())
                .usersId(order.getUsers().getUserId())
                .build();
    }
}
