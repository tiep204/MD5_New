package ra.model.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.model.entity.Product;
import ra.payload.request.ProductRequest;
import ra.payload.response.ProductDTO;
import ra.payload.response.ProductShort;

import java.util.List;
import java.util.Map;

public interface ProductService {
    List<Product> findAll();

    Product findById(int productID);

    ProductDTO findByIdD(int productID);

    Product saveOrUpdate(Product product);

    List<Product> searchByName(String productName);

    Page<Product> getPaging(Pageable pageable);
/*    Page<ProductShort> getPage(Pageable pageable);*/
    Map<String,Object> paging(int page,int size);

    void delete(int productID);

    void deleteFromWishList(int productID);

    List<Product> getWishlist(int userId);

    List<ProductShort> getAllProductShorts();

    ProductDTO createProduct(ProductRequest productRequest);

    ProductDTO productDetail(int id);

    ProductDTO updateProduct(int productID, ProductRequest productRequest);
}
