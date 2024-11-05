package ee.maksuamet.service;

import ee.maksuamet.domain.Company;
import ee.maksuamet.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public List<Company> getAllCompanies() {
        return companyRepository.getCompanies();
    }

    public List<Company> getCompaniesByQuery(String query) {
        return companyRepository.getCompaniesByQuery(query);

    }
}
