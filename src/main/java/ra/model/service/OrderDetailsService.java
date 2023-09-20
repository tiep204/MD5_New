package ra.model.service;

import ra.model.entity.OrderDetails;
import ra.payload.response.OrderDetailsResponse;

import java.util.List;

public interface OrderDetailsService {
    List<OrderDetails> getAllOrderDetails(int orderID);
    OrderDetails save(OrderDetails orderDetails);
    void saveOrder();
    List<OrderDetailsResponse> getAllOrderDetail(int orderID);
}
