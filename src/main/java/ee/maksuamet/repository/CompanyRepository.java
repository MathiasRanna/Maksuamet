package ee.maksuamet.repository;

import ee.maksuamet.domain.Company;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CompanyRepository {
    private final String csvFilePath = "src/main/resources/csv_files/tasutud_maksud_2024_iii_kvartal.csv";

    public List<Company> getCompaniesByQuery(String query) {
        List<Company> companies = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // skip header line
                }
                Company company = getCompany(line);
                if ( (company.getName().toLowerCase().contains(query.toLowerCase()) ||
                        company.getRegistryCode().contains(query)) &
                        companies.size() < 50) {
                    companies.add(company);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return companies;
    }
    @Nullable
    public Optional<Company> getCompanyByRegistrikood(String registrikood) {
        Company company = null;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // skip header line
                }
                Company companyCsv = getCompany(line);
                if (companyCsv.getRegistryCode().equals(registrikood)) {
                    company = companyCsv;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(company);
    }

    private static Company getCompany(String line) {
        String[] values = parseCsvLine(line);
        Company company = new Company();
        company.setRegistryCode(values.length > 0 ? values[0] : "");
        company.setName(values.length > 1 ? values[1] : "");
        company.setType(values.length > 2 ? values[2] : "");
        company.setVATPayer(values.length > 3 ? values[3] : "");
        company.setArea(values.length > 4 ? values[4] : "");
        company.setRegion(values.length > 5 ? values[5] : "");
        company.setStateTaxes(values.length > 6 && !values[6].isEmpty() ? Float.parseFloat(values[6].replace(",", ".")) : 0.0f);
        company.setLaborTaxes(values.length > 7 && !values[7].isEmpty() ? Float.parseFloat(values[7].replace(",", ".")) : 0.0f);
        company.setTurnover(values.length > 8 && !values[8].isEmpty() ? Float.parseFloat(values[8].replace(",", ".")) : 0.0f);
        company.setEmployees(values.length > 9 && !values[9].isEmpty() ? Integer.parseInt(values[9]) : 0);
        return company;
    }

    private static String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                inQuotes = !inQuotes;
            } else if (ch == ';' && !inQuotes) {
                values.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(ch);
            }
        }
        values.add(sb.toString().trim()); // Add the last value
        return values.toArray(new String[0]);
    }



}
