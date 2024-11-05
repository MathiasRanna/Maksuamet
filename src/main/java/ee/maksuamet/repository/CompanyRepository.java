package ee.maksuamet.repository;

import ee.maksuamet.domain.Company;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

@Repository
public class CompanyRepository {
    private final String csvFilePath = "src/main/resources/csv_files/tasutud_maksud_2024_iii_kvartal.csv";

    public List<Company> getCompanies() {
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
                companies.add(company);

                if(companies.size() > 50){ break; }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return companies;
    }

    public List<Company> getCompaniesByQuery(String query) {
        return getCompanies().stream()
                .filter(company -> company.getNimi().toLowerCase().contains(query.toLowerCase()) ||
                        company.getRegistrikood().contains(query))
                .limit(50)
                .collect(Collectors.toList());
    }

    private static Company getCompany(String line) {
        String[] values = parseCsvLine(line);
        Company company = new Company();
        company.setRegistrikood(values.length > 0 ? values[0] : "");
        company.setNimi(values.length > 1 ? values[1] : "");
        company.setLiik(values.length > 2 ? values[2] : "");
        company.setKMK(values.length > 3 ? values[3] : "");
        company.setValdkond(values.length > 4 ? values[4] : "");
        company.setMaakond(values.length > 5 ? values[5] : "");
        company.setRiiklikudMaksud(values.length > 6 && !values[6].isEmpty() ? Float.parseFloat(values[6].replace(",", ".")) : 0.0f);
        company.setToojouMaksud(values.length > 7 && !values[7].isEmpty() ? Float.parseFloat(values[7].replace(",", ".")) : 0.0f);
        company.setKaive(values.length > 8 && !values[8].isEmpty() ? Float.parseFloat(values[8].replace(",", ".")) : 0.0f);
        company.setTootajaid(values.length > 9 && !values[9].isEmpty() ? Integer.parseInt(values[9]) : 0);
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
