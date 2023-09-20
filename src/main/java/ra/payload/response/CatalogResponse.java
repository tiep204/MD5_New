package ra.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogResponse {
    private int catalogID;
    private String catalogName;
    private String catalogTitle;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date created;
    private boolean catalogStatus;
}