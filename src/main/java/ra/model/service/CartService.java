package ra.model.service;

import ra.model.entity.Cart;
import ra.payload.request.CartRequest;
import ra.payload.response.CartDTO;

import java.util.List;

public interface CartService {
    Cart insertCart(Cart cart);
    List<Cart> findAllUserCartById(int userId);
    void delete(int cartID);
    Cart findCartByID(int cartID);
    List<CartDTO> findAllCart(List<Cart> list);
    void updateCart(int cartId ,int quantity);
    void createCart(CartRequest cartRequest);
}
