package ra.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.model.entity.Cart;

import java.util.Date;

import java.util.List;
import java.util.Set;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String address;
    private Date created;
    private boolean userStatus;
    private Set<String> listRoles;
}