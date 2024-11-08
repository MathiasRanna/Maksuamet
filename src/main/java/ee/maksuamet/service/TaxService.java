package ee.maksuamet.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Getter
@Service
public class TaxService {

    @Value("${tax.social.percentage}")
    private double SOCIAL_TAX_PERCENTAGE;

    @Value("${tax.income.percentage}")
    private double INCOME_TAX_PERCENTAGE;

    @Value("${tax.pension.percentage}")
    private double PENSION_PERCENTAGE;

    @Value("${tax.unemploymentinsurance.employer.percentage}")
    private double UNEMPLOYMENT_INSURANCE_EMPLOYER_PERCENTAGE;

    @Value("${tax.unemploymentinsurance.employee.percentage}")
    private double UNEMPLOYMENT_INSURANCE_EMPLOYEE_PERCENTAGE;


    // Methods to calculate the values based on gross salary
    public double calculateSocialTax(double grossSalary) {
        return Math.round(grossSalary * SOCIAL_TAX_PERCENTAGE / 100);
    }

    public double calculateIncomeTax(double grossSalary) {
        return Math.round(grossSalary * INCOME_TAX_PERCENTAGE / 100);
    }

    public double calculatePension(double grossSalary) {
        return Math.round(grossSalary * PENSION_PERCENTAGE / 100);
    }

    public double calculateUnemploymentInsuranceEmployer(double grossSalary) {
        return Math.round(grossSalary * UNEMPLOYMENT_INSURANCE_EMPLOYER_PERCENTAGE / 100);
    }

    public double calculateUnemploymentInsuranceEmployee(double grossSalary) {
        var myAnswer = Math.round(grossSalary * UNEMPLOYMENT_INSURANCE_EMPLOYEE_PERCENTAGE / 100);
        return Math.round(grossSalary * UNEMPLOYMENT_INSURANCE_EMPLOYEE_PERCENTAGE / 100);
    }

    public double calculateEmployerCost(double grossSalary) {
        return Math.round(grossSalary + calculateUnemploymentInsuranceEmployer(grossSalary) + calculateSocialTax(grossSalary));
    }

    public double calculateNetSalary(double grossSalary) {
        return Math.round(grossSalary - (calculateUnemploymentInsuranceEmployee(grossSalary) + calculatePension(grossSalary) + calculateIncomeTax(grossSalary)));
    }

    public double calculateBrutoSalaryBasedOnTaxes(double taxes) {
        return Math.round(taxes / ((
                SOCIAL_TAX_PERCENTAGE +
                        INCOME_TAX_PERCENTAGE +
                PENSION_PERCENTAGE +
                UNEMPLOYMENT_INSURANCE_EMPLOYER_PERCENTAGE +
                UNEMPLOYMENT_INSURANCE_EMPLOYEE_PERCENTAGE
                ) / 100));
    }


}
