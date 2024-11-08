package ee.maksuamet.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quarter_id", nullable = false)
    private Quarter quarter;

    @Column(nullable = false)
    private String registryCode;

    private String name;
    private String type;
    private String VATPayer;
    private String Area;
    private String Region;
    private float stateTaxes;
    private float laborTaxes;
    private float turnover;
    private int employees;
}