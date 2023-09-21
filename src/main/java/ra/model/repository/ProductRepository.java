package ra.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ra.model.entity.Product;
import ra.payload.response.ProductDTO;
import ra.payload.response.ProductShort;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> searchByProductNameContaining(String productName);
    
    @Query(value = "SELECT p.id,p.description,p.status,p.quantity ,p.product_name, p.price, p.title, p.image, p.catalog_id " +
            "FROM product p " +
            "JOIN wishlist w ON p.id = w.product_id " +
            "WHERE w.user_id = :userId", nativeQuery = true)
    List<Product> findProductsInWishlist(@Param("userId") int userId);
}