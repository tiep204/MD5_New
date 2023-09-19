package ra.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private int orderID;
    private float totalAmount;
    private String orderStatus;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date created;
    private int usersId;
}