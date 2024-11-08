package ee.maksuamet.domain;

import lombok.*;

@Getter
@Setter
public class Company {
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