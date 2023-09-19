package ra.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ra.model.entity.Product;
import ra.model.entity.Users;
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

    @GetMapping("/getAllWishlist")
    @PreAuthorize("hasRole('USER')")
    public List<ProductShort> getAllWishlist() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Product> wishlist = productService.getWishlist(userDetails.getUserId());
        System.out.println("=====================================================================>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+userDetails.getUserId());
        List<ProductShort> wishlistShort = new ArrayList<>();
        for (Product product : wishlist) {
            ProductShort productShort = new ProductShort();
            productShort.setProductID(product.getProductID());
            productShort.setProductName(product.getProductName());
            productShort.setProductTitle(product.getProductTitle());
            productShort.setImage(product.getImage());
            productShort.setPrice(product.getPrice());
            productShort.setCatalog(product.getCatalog().getCatalogName());
            wishlistShort.add(productShort);
        }

        return wishlistShort;
    }
}