package ra.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Orders")
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int orderID;
    @Column(name = "total_amount")
    private float totalAmount;
    @Column(name = "order_status")
    private String orderStatus;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "created_at")
    private Date created;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Users users;
    @JsonIgnore
    @OneToMany(mappedBy = "order")
    private List<OrderDetails> listOrderDetails = new ArrayList<>();
}