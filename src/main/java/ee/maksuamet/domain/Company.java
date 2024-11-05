package ee.maksuamet.domain;

import lombok.*;

@Getter
@Setter
public class Company {
    private String registrikood;
    private String nimi;
    private String liik;
    private String KMK;
    private String valdkond;
    private String maakond;
    private float riiklikudMaksud;
    private float toojouMaksud;
    private float kaive;
    private int tootajaid;
}