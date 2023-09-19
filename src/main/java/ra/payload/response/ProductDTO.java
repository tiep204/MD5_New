package ra.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.model.entity.ProductImage;

import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private String productName;
    private float price;
    private int quantity;
    private String productTitle;
    private String descriptions;
    private String image;
    private String catalog;
    private List<ProductImage> listImageLink = new ArrayList<>();
    private boolean productStatus;
}