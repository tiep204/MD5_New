package ra.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private int cartID;
    private String productName;
    private int quantity;
    private float price;
    private float totalPrice;
}