package ee.maksuamet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyDTO {

    // Getters and Setters
    private String registryCode;
    private String name;
    private int employeeCount;
    private double employerCost;

    private double socialTax;
    private double unemploymentInsuranceEmployer;

    private double brutoSalary;

    private double incomeTax;
    private double pension;
    private double unemploymentInsuranceEmployee;

    private double netoSalary;

    public CompanyDTO(String RegistryCode, String name) {
        this.registryCode = RegistryCode;
        this.name = name;
    }
}

