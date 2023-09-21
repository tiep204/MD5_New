package ra.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {
    private int productID;
    private String productName;
    private float price;
    private int quantity;
    private String productTitle;
    private String descriptions;
    /*private String image;*/
    private int catalogID;
    private List<MultipartFile> listImageLink;
    private boolean productStatus;
}
