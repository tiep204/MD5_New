package ra.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name = "Catalog")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Catalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int catalogID;
    @Column(name = "catalog_name", nullable = false, unique = true)
    private String catalogName;
    @Column(name = "catalog_title", columnDefinition = "text")
    private String catalogTitle;
    @Column(name = "create_at")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date created;
    @Column(name = "status")
    private boolean catalogStatus;
}