package ra.model.service.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ra.model.entity.Cart;
import ra.model.service.ProductService;
import ra.payload.request.CartRequest;
import ra.payload.response.CartDTO;
@Component
public class CartMapper implements IGenericMapper<Cart, CartRequest, CartDTO>{
    @Autowired
    private ProductService productService;
    @Override
    public Cart toEntity(CartRequest cartRequest) {
        return Cart.builder()
                .quantity(cartRequest.getQuantity())
                .product(productService.findById(cartRequest.getProductID()))
                .build();
    }

    @Override
    public CartDTO toResponse(Cart cart) {
        return CartDTO.builder()
                .cartID(cart.getCartID())
                .quantity(cart.getQuantity())
                .price(cart.getPrice())
                .totalPrice(cart.getTotalPrice())
                .build();
    }
}