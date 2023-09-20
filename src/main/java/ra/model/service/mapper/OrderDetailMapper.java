package ra.model.service.mapper;

import org.springframework.stereotype.Component;
import ra.model.entity.OrderDetails;
import ra.payload.request.OrderDetailRequest;
import ra.payload.response.OrderDetailsResponse;
@Component
public class OrderDetailMapper implements IGenericMapper<OrderDetails, OrderDetailRequest, OrderDetailsResponse>{
    @Override
    public OrderDetails toEntity(OrderDetailRequest orderDetailRequest) {
        return OrderDetails.builder()
                .order(orderDetailRequest.getOrder())
                .product(orderDetailRequest.getProduct())
                .price(orderDetailRequest.getPrice())
                .quantity(orderDetailRequest.getQuantity())
                .total(orderDetailRequest.getTotal())
                .build();
    }

    @Override
    public OrderDetailsResponse toResponse(OrderDetails orderDetails) {
        return OrderDetailsResponse.builder()
                .productName(orderDetails.getProduct().getProductName())
                .price(orderDetails.getPrice())
                .quantity(orderDetails.getQuantity())
                .total(orderDetails.getTotal())
                .build();
    }
}