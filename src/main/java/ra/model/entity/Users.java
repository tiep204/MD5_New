package ra.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "Users")
@Data
@NamedEntityGraph
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int userId;
    @Column(name = "username",unique = true,nullable = false)
    private String userName;
    @Column(name = "password",nullable = false)
    private String password;
    @Column(name = "create_at")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date created;
    @Column(name = "email",nullable = false,unique = true)
    private String email;
    @Column(name = "phone_number")
    private String phone;
    @Column(name = "address")
    private String address;
    @Column(name = "status")
    private boolean userStatus;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Roles> listRoles = new HashSet<>();
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "wishlist",joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> listProduct = new HashSet<>();
}