package ra.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private int productID;
    private String productName;
    private float price;
    private int quantity;
    private String productTitle;
    private String descriptions;
    private String image;
    private int catalogID;
    private List<String> listImageLink;
    private boolean productStatus;
}
