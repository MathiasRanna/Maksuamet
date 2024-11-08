package ee.maksuamet.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Quarter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String year;

    @Column(nullable = false)
    private int quarter;

    @Column(unique = true, nullable = false)
    private String fileName;

    @OneToMany(mappedBy = "quarter")
    private List<Company> companies;
}

