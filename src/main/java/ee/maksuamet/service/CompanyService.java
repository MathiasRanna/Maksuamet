package ee.maksuamet.service;

import ee.maksuamet.domain.Company;
import ee.maksuamet.dto.CompanyDTO;
import ee.maksuamet.helpers.Helpers;
import ee.maksuamet.repository.CompanyRepository;
import ee.maksuamet.repository.QuarterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private QuarterRepository quarterRepository;

    @Autowired
    private TaxService taxService;

    private final Helpers helpers = new Helpers();

    private static final Pattern QUARTER_PATTERN = Pattern.compile("(i{1,3}|iv)_(\\d{4})", Pattern.CASE_INSENSITIVE);


    public Optional<CompanyDTO> getCompanyBySalaryDetails(String registryCode, String quarter) {

        Matcher matcher = QUARTER_PATTERN.matcher(quarter);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid quarter format: " + quarter);
        }
        int quarterValue = helpers.quarterToInt(matcher.group(1).toUpperCase());
        String year = matcher.group(2);

        Optional<Company> companyOptional = companyRepository.findByRegistryCodeByQuarter(registryCode, year, quarterValue);

        if (companyOptional.isEmpty()) {return Optional.empty();}
        CompanyDTO companyDTO = mapCompanyToDTO(companyOptional.get());

        Optional<Company> previousQuarterCompanyOptional = companyRepository.findPreviousQuarterByRegistryCode(registryCode, year, quarterValue);
        previousQuarterCompanyOptional.ifPresent(previousQuarterCompany -> {
            CompanyDTO previousCompanyDTO = mapCompanyToDTO(previousQuarterCompany);
            companyDTO.setPreviousQuaterEmployeeCount(previousCompanyDTO.getEmployeeCount());
            companyDTO.setPreviousQuaterSalary(previousCompanyDTO.getNetoSalary());
        });

        return Optional.of(companyDTO);
    }

    private CompanyDTO mapCompanyToDTO(Company company) {
        if (company.getEmployees() == 0) {
            CompanyDTO companyDTO = new CompanyDTO(company.getRegistryCode(), company.getName());
            companyDTO.setEmployeeCount(0);
            companyDTO.setBrutoSalary(0);
            companyDTO.setNetoSalary(0);
            return companyDTO;
        }
        double monthlyTaxPerEmployee = (company.getLaborTaxes() / 3) / company.getEmployees();

        CompanyDTO companyDTO = new CompanyDTO(company.getRegistryCode(), company.getName());
        companyDTO.setEmployeeCount(company.getEmployees());
        double averageBrutoSalary = taxService.calculateBrutoSalaryBasedOnTaxes(monthlyTaxPerEmployee);
        companyDTO.setEmployerCost(taxService.calculateEmployerCost(averageBrutoSalary));
        companyDTO.setSocialTax(taxService.calculateSocialTax(averageBrutoSalary));
        companyDTO.setUnemploymentInsuranceEmployer(taxService.calculateUnemploymentInsuranceEmployer(averageBrutoSalary));

        companyDTO.setBrutoSalary(averageBrutoSalary);
        companyDTO.setIncomeTax(taxService.calculateIncomeTax(averageBrutoSalary));
        companyDTO.setPension(taxService.calculatePension(averageBrutoSalary));
        companyDTO.setUnemploymentInsuranceEmployee(taxService.calculateUnemploymentInsuranceEmployee(averageBrutoSalary));

        companyDTO.setNetoSalary(taxService.calculateNetSalary(averageBrutoSalary));

        return companyDTO;
    }

    public List<Company> getCompaniesByQuery(String query, String quarter) {
        Matcher matcher = QUARTER_PATTERN.matcher(quarter);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid quarter format: " + quarter);
        }
        int quarterValue = helpers.quarterToInt(matcher.group(1).toUpperCase());
        String year = matcher.group(2);
        return companyRepository.findByNameContainingIgnoreCaseOrRegistryCodeContainingIgnoreCase(query, year, quarterValue);
    }

    public List<String> listAllQuarters() {
        return quarterRepository.findAll().stream()
                .sorted((q1, q2) -> {
                    int yearComparison = Integer.compare(Integer.parseInt(q2.getYear()), Integer.parseInt(q1.getYear()));
                    if (yearComparison == 0) {
                        return Integer.compare(q2.getQuarter(), q1.getQuarter());
                    }
                    return yearComparison;
                })
                .map(quarter -> helpers.IntToQuarter(quarter.getQuarter()) + "_" + quarter.getYear())
                .collect(Collectors.toList());
    }

}
