package ee.maksuamet.service;

import ee.maksuamet.domain.Company;
import ee.maksuamet.dto.CompanyDTO;
import ee.maksuamet.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    private final TaxService taxService;
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository, TaxService taxService) {
        this.companyRepository = companyRepository;
        this.taxService = taxService;
    }

    public List<Company> getCompaniesByQuery(String query) {
        return companyRepository.getCompaniesByQuery(query);
    }

    public Optional<CompanyDTO> getCompanyBySalaryDetails(String registryCode) {
        Company company = companyRepository.getCompanyByRegistrikood(registryCode).orElse(null);
        if (company == null) {
            return Optional.empty();
        }

        double monthlyTaxPerEmployee = (company.getLaborTaxes() / 3) / company.getEmployees();

        CompanyDTO copmanyDTO = new CompanyDTO(company.getRegistryCode(), company.getName());
        copmanyDTO.setEmployeeCount(company.getEmployees());
        double averageBrutoSalary = taxService.calculateBrutoSalaryBasedOnTaxes(monthlyTaxPerEmployee);
        copmanyDTO.setEmployerCost(taxService.calculateEmployerCost(averageBrutoSalary));
        copmanyDTO.setSocialTax(taxService.calculateSocialTax(averageBrutoSalary));
        copmanyDTO.setUnemploymentInsuranceEmployer(taxService.calculateUnemploymentInsuranceEmployer(averageBrutoSalary));

        copmanyDTO.setBrutoSalary(averageBrutoSalary);
        copmanyDTO.setIncomeTax(taxService.calculateIncomeTax(averageBrutoSalary));
        copmanyDTO.setPension(taxService.calculatePension(averageBrutoSalary));
        copmanyDTO.setUnemploymentInsuranceEmployee(taxService.calculateUnemploymentInsuranceEmployee(averageBrutoSalary));

        copmanyDTO.setNetoSalary(taxService.calculateNetSalary(averageBrutoSalary));

        return Optional.ofNullable(copmanyDTO);
    }

    public List<String> listAllQuarters() throws IOException {
        return CompanyRepository.listAllQuarters();
    }

    public String setCsvPath(String csvPath) throws IOException {
        return CompanyRepository.setCsvFilePath(csvPath);
    }

}
