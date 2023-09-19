package ra.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int productID;
    @Column(name = "product_name", unique = true, nullable = false)
    private String productName;
    @Column(name = "price")
    private float price;
    @Column(name = "quantity", nullable = false)
    private int quantity;
    @Column(name = "title", columnDefinition = "text")
    private String productTitle;
    @Column(name = "description", columnDefinition = "text")
    private String descriptions;
    @Column(name = "image")
    private String image;
    @ManyToOne
    @JoinColumn(name = "catalog_id", nullable = false)
    private Catalog catalog;
    @Column(name = "status")
    private boolean productStatus;
    @OneToMany(mappedBy = "product")
    private List<ProductImage> listImageLink = new ArrayList<>();
}
