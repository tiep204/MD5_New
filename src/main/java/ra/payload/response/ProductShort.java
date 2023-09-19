package ra.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductShort {
    private int productID;
    private String productName;
    private float price;
    private String productTitle;
    private String catalog;
    private String image;
}