package ra.model.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import ra.model.entity.Product;
import ra.payload.request.ProductRequest;
import ra.payload.response.ProductDTO;
import ra.payload.response.ProductShort;

import java.util.List;

public interface ProductService {
    List<Product> findAll();
    Product findById(int productID);
    Product saveOrUpdate(Product product);
    List<Product> searchByName(String productName);
    Page<Product> getPaging(Pageable pageable);
    void delete(int productID);
    void deleteFromWishList(int productID);
    List<Product> getWishlist(int userId);
    List<ProductShort> getAllProductShorts();
    Product createProduct(ProductRequest productRequest);
    ProductDTO productDetail(int id);
    void updateProduct(int productID,ProductRequest productRequest);
}
