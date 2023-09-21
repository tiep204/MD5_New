package ra.model.service.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ra.model.entity.Cart;
import ra.model.entity.Product;
import ra.model.entity.Users;
import ra.model.repository.CartRepository;
import ra.model.service.CartService;
import ra.model.service.ProductService;
import ra.model.service.UserService;
import ra.payload.request.CartRequest;
import ra.payload.response.CartDTO;
import ra.payload.response.MessageResponse;
import ra.security.CustomUserDetails;

import javax.persistence.EntityExistsException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImp implements CartService {
    @Autowired
    CartRepository cartRepository;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Override
    public Cart insertCart(Cart cart) {
        return cartRepository.save(cart);
    }

    @Override
    public List<Cart> findAllUserCartById(int userId) {
        return cartRepository.findByUsers_UserId(userId);
    }

    @Override
    public void delete(int cartID) {
        cartRepository.deleteById(cartID);
    }

    @Override
    public Cart findCartByID(int cartID) {
        return cartRepository.findById(cartID).get();
    }

    @Override
    public List<CartDTO> findAllCart(List<Cart> listCart) {
        List<CartDTO> listCartDTO = new ArrayList<>();
        for (Cart cart : listCart) {
            CartDTO cartDTO = new CartDTO();
            cartDTO.setCartID(cart.getCartID());
            cartDTO.setQuantity(cart.getQuantity());
            cartDTO.setPrice(cart.getProduct().getPrice());
            cartDTO.setTotalPrice(cart.getTotalPrice());
            cartDTO.setProductName(cart.getProduct().getProductName());
            listCartDTO.add(cartDTO);
        }
        return listCartDTO;
    }

    @Override
    public void updateCart(int cartId, int quantity) {
        Cart cart = findCartByID(cartId);
        if (quantity > 0) {
            cart.setQuantity(quantity);
            cart.setTotalPrice(cart.getProduct().getPrice() * cart.getQuantity());
            insertCart(cart);
        } else {
            delete(cartId);
        }
    }

    @Override
    public void createCart(CartRequest cartRequest) throws EntityExistsException {
        boolean check = false;
        Cart cart = new Cart();
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Cart> listCart = findAllUserCartById(userDetails.getUserId());
        Product product = productService.findById(cartRequest.getProductID());
        Users users = userService.getUserByID(userDetails.getUserId());
        for (Cart cartExist : listCart) {
            if (cartExist.getProduct().getProductID() == product.getProductID()) {
                cart = cartExist;
                check = true;
                break;
            }
        }
        if (check) {
            if (cartRequest.getQuantity() <= product.getQuantity()) {
                System.out.println(cartRequest.getQuantity());
                cart.setQuantity(cartRequest.getQuantity() + cart.getQuantity());
                cart.setTotalPrice(cart.getProduct().getPrice() * cart.getQuantity());
                insertCart(cart);
            } else {
                throw new EntityExistsException("Số lượng hàng còn lại trong kho không đủ! Vui lòng chọn lại");
            }
        } else {
            if (cartRequest.getQuantity() <= product.getQuantity()) {
                Cart cartNew = new Cart();
                cartNew.setQuantity(cartRequest.getQuantity());
                cartNew.setUsers(users);
                cartNew.setProduct(product);
                cartNew.setPrice(product.getPrice());
                cartNew.setTotalPrice(cartNew.getProduct().getPrice() * cartNew.getQuantity());
                insertCart(cartNew);
            } else {
                throw new EntityExistsException("Số lượng hàng còn lại trong kho không đủ! Vui lòng chọn lại");
            }
        }
    }
}
