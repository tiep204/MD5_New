package ra.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ra.model.entity.Product;
import ra.model.entity.Users;
import ra.model.repository.UserRepository;
import ra.model.service.ProductService;
import ra.model.service.UserService;
import ra.payload.response.MessageResponse;
import ra.payload.response.ProductShort;
import ra.security.CustomUserDetails;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("api/v1/wishlist")
public class WishlistController {
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @PostMapping("/addToWishList/{productID}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addToWishList(@PathVariable("productID") int productID){
        try {
            Product product = productService.findById(productID);
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Users users = userService.getUserByID(userDetails.getUserId());
            users.getListProduct().add(product);
            userService.saveOrUpdate(users);
            return ResponseEntity.ok(new MessageResponse("da them vao wishlist"));
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageResponse("Có lỗi trong quá trình xử lý vui lòng thử lại!"));
        }
    }
    @DeleteMapping("/DeleteWishlist/{productID}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteWishList(@PathVariable("productID") int productID){
        try {
            Product product = productService.findById(productID);
            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Users users = userService.getUserByID(userDetails.getUserId());
            users.getListProduct().remove(product);
            userService.saveOrUpdate(users);
            return ResponseEntity.ok(new MessageResponse("da xoa khoi wishlist"));
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageResponse("Có lỗi trong quá trình xử lý vui lòng thử lại!"));
        }
    }

    @GetMapping("/getAllWishlist")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getAllWishlist() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users users = userRepository.findById(userDetails.getUserId()).get();
        List<Product> wishlist = productService.getWishlist(users.getUserId());
        return new ResponseEntity<>(wishlist, HttpStatus.OK);
    }
}