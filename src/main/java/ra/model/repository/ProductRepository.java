package ra.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ra.model.entity.Product;
import ra.payload.response.ProductDTO;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {
    List<Product> searchByProductNameContaining(String productName);


    @Query(value = "select p.id,p.product_name,p.price,p.title,p.image,p.catalog_id from product p join wishlist w on p.id = w.product_id where w.user_id = :uID",nativeQuery = true)
    List<Product> getAllWishList(@Param("uID") int userId);

}
