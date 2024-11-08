package ee.maksuamet.api;

import ee.maksuamet.domain.Company;
import ee.maksuamet.dto.CompanyDTO;
import ee.maksuamet.service.CompanyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/")
    public String displayCompanies() {
        return "index";
    }

    @GetMapping("/search")
    @ResponseBody
    public List<Map<String, String>> searchCompanies(@RequestParam("query") String query) {
        List<Company> companies = companyService.getCompaniesByQuery(query);
        List<Map<String, String>> results = new ArrayList<>();
        for (Company company : companies) {
            Map<String, String> companyInfo = new HashMap<>();
            companyInfo.put("nimi", company.getName());
            companyInfo.put("registrikood", company.getRegistryCode());
            results.add(companyInfo);
        }
        return results;
    }

    @GetMapping("/company-details")
    @ResponseBody
    public CompanyDTO getCompanyByRegistrikood(@RequestParam("id") String registryCode) {
        Optional<CompanyDTO> company = companyService.getCompanyBySalaryDetails(registryCode);
        return company.orElse(null);
    }

}
