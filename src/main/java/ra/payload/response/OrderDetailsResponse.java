package ra.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsResponse {
    private String productName;
    private float price;
    private int quantity;
    private float total;
}