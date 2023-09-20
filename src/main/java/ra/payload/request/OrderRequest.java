package ra.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.model.entity.OrderDetails;
import ra.model.entity.Users;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private int orderID;
    private float totalAmount;
    private String orderStatus;
    private Date created;
    @JsonIgnore
    private Users users;
    private List<OrderDetails> listOrderDetails = new ArrayList<>();
}