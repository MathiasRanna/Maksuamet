package ee.maksuamet.api;

import ee.maksuamet.domain.Company;
import ee.maksuamet.dto.CompanyDTO;
import ee.maksuamet.repository.CompanyRepository;
import ee.maksuamet.service.CompanyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.*;

@Controller
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @GetMapping("/")
    public String displayCompanies(Model model) {
        model.addAttribute("quarters", companyService.listAllQuarters());
        return "index";
    }

    @GetMapping("/search")
    @ResponseBody
    public List<Map<String, String>> searchCompanies(@RequestParam("query") String query, @RequestParam("quarter") String quarter) {
        List<Company> companies = companyService.getCompaniesByQuery(query, quarter);
        List<Map<String, String>> results = new ArrayList<>();
        for (Company company : companies) {
            Map<String, String> companyInfo = new HashMap<>();
            companyInfo.put("name", company.getName());
            companyInfo.put("registryCode", company.getRegistryCode());
            results.add(companyInfo);
        }
        return results;
    }

    @GetMapping("/company-details")
    @ResponseBody
    public CompanyDTO getCompanyByRegistryCode(@RequestParam("registryCode") String registryCode, @RequestParam("quarter") String quarter) {
        Optional<CompanyDTO> company = companyService.getCompanyBySalaryDetails(registryCode, quarter);
        return company.orElse(null);
    }

}
