package ra.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ra.model.entity.Catalog;
import ra.model.entity.Product;
import ra.model.entity.ProductImage;
import ra.model.entity.Users;
import ra.model.service.CatalogService;
import ra.model.service.ImageService;
import ra.model.service.ProductService;
import ra.model.service.UserService;
import ra.payload.request.ProductRequest;
import ra.payload.response.MessageResponse;
import ra.payload.response.ProductDTO;
import ra.payload.response.ProductShort;
import ra.security.CustomUserDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/product")
public class ProductController {
    @Autowired
    ProductService productService;
    @Autowired
    CatalogService catalogService;
    @Autowired
    ImageService imageService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
/*
    @PreAuthorize("hasRole('ADMIN')")
*/
    public ResponseEntity<List<ProductShort>> getAll() {
        return new ResponseEntity<>(productService.getAllProductShortsAdmin(), HttpStatus.OK);
    }

/*    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ProductShort>> getAllProduct() {
        return new ResponseEntity<>(productService.getAllProductShorts(), HttpStatus.OK);
    }*/

/*    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProductRequest product) {
        try {
            Catalog catalog = catalogService.findById(product.getCatalogID());
            Product proNew = new Product();
            proNew.setCatalog(catalog);
            proNew.setProductName(product.getProductName());
            proNew.setProductTitle(product.getProductTitle());
            proNew.setPrice(product.getPrice());
            proNew.setQuantity(product.getQuantity());
            proNew.setImage(product.getImage());
            proNew.setDescriptions(product.getDescriptions());
            proNew.setProductStatus(true);
            productService.saveOrUpdate(proNew);
            for (String str : product.getListImageLink()) {
                ProductImage image = new ProductImage();
                image.setUrlImage(str);
                image.setProduct(proNew);
                imageService.save(image);
            }
            return ResponseEntity.ok(new MessageResponse("Create product successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return Respon¬
            seEntity.badRequest().body(new MessageResponse("Có lỗi trong quá trình xử lý vui lòng thử lại!"));
        }
    }*/

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@ModelAttribute ProductRequest product) {
        try {
            productService.createProduct(product);
            return ResponseEntity.ok(new MessageResponse("Thêm mới thành công"));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageResponse("Có lỗi trong quá trình xử lý vui lòng thử lại!"));
        }
    }


    @PutMapping("/{productID}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable("productID") int productID, @ModelAttribute ProductRequest product) {
        try {
            productService.updateProduct(productID, product);
            return ResponseEntity.ok(new MessageResponse("Update product successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageResponse("Có lỗi trong quá trình xử lý vui lòng thử lại!"));
        }
    }


    @GetMapping("/{productName}")
    public List<Product> searchByName(@PathVariable("productName") String productName) {
        return productService.searchByName(productName);
    }

    @GetMapping("/delete/{productID}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable("productID") int productID) {
        Product product = productService.findById(productID);
        if (product.isProductStatus() == true) {
            product.setProductStatus(false);
            productService.saveOrUpdate(product);
            return ResponseEntity.ok(new MessageResponse("Delete product success"));
        } else {
            return ResponseEntity.ok(new MessageResponse("delete product No success"));
        }

    }

    @GetMapping("/detail/{productID}")
    public ResponseEntity<?> findById(@PathVariable("productID") int productID) {
        try {
            ProductDTO productDTO = productService.findByIdD(productID);
            return ResponseEntity.ok(productDTO);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageResponse("Không thể tìm thấy sản phẩm"));
        }
    }

    @GetMapping("/paging")
    public ResponseEntity<?> getPaging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size) {
        Map<String, Object> data = productService.paging(page, size);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}