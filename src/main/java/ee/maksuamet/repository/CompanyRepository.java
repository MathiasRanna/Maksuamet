package ee.maksuamet.repository;

import ee.maksuamet.domain.Company;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Repository
public class CompanyRepository {

    // Default file path
    private static String CSV_FILE = "tasutud_maksud_2024_iii_kvartal.csv";
    private static final String CSV_DIR = "src/main/resources/csv_files/";
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("tasutud_maksud_(\\d{4})_(i{1,3}|iv)_kvartal\\.csv", Pattern.CASE_INSENSITIVE);
    private static final Pattern FORMATED_NAME_PATTERN = Pattern.compile("(I{1,3}|IV)_(\\d{4})", Pattern.CASE_INSENSITIVE);

    public static String setCsvFilePath(String quarterString) {
        Matcher matcher = FORMATED_NAME_PATTERN.matcher(quarterString);
        if (matcher.matches()) {
            String quarter = matcher.group(1).toLowerCase();
            String year = matcher.group(2);
            CSV_FILE = String.format("tasutud_maksud_%s_%s_kvartal.csv", year, quarter);
            return String.format("OK - file set to %s", CSV_FILE);
        } else {
            throw new IllegalArgumentException("Invalid quarter format: " + quarterString);
        }
    }
    public static List<String> listAllQuarters() throws IOException {
        List<String> files = Files.list(Paths.get(CSV_DIR))
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .map(Path::toString)
                .filter(fileName -> FILE_NAME_PATTERN.matcher(fileName).matches())
                .map(CompanyRepository::formatQuarter)
                .sorted(CompanyRepository::compareQuarters)
                .collect(Collectors.toList());
        setCsvFilePath(files.get(0));
        return files;
    }

    public List<Company> getCompaniesByQuery(String query) {
        List<Company> companies = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_DIR + CSV_FILE))) {
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
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_DIR + CSV_FILE))) {
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


    // Utility method to convert file name to quarter format (e.g., "II 2024")
    private static String formatQuarter(String fileName) {
        Matcher matcher = FILE_NAME_PATTERN.matcher(fileName);
        if (matcher.matches()) {
            String year = matcher.group(1);
            String quarter = matcher.group(2).toUpperCase();
            switch (quarter) {
                case "I": return "I_" + year;
                case "II": return "II_" + year;
                case "III": return "III_" + year;
                case "IV": return "IV_" + year;
                default: throw new IllegalArgumentException("Unexpected quarter: " + quarter);
            }
        }
        throw new IllegalArgumentException("Invalid file name format: " + fileName);
    }

    private static int compareQuarters(String file1, String file2) {
        Matcher matcher1 = FORMATED_NAME_PATTERN.matcher(file1);
        Matcher matcher2 = FORMATED_NAME_PATTERN.matcher(file2);

        if (matcher1.matches() && matcher2.matches()) {
            // Correct group indexing: group(2) for year and group(1) for quarter
            int year1 = Integer.parseInt(matcher1.group(2));
            int year2 = Integer.parseInt(matcher2.group(2));

            // Compare years first
            if (year1 != year2) {
                return Integer.compare(year2, year1);
            }

            // Compare quarters if years are the same
            String quarter1 = matcher1.group(1).toUpperCase();
            String quarter2 = matcher2.group(1).toUpperCase();

            return Integer.compare(quarterToInt(quarter2), quarterToInt(quarter1));
        } else {
            throw new IllegalArgumentException("Invalid file name format for comparison: " + file1 + " or " + file2);
        }
    }

    private static int quarterToInt(String quarter) {
        switch (quarter) {
            case "I": return 1;
            case "II": return 2;
            case "III": return 3;
            case "IV": return 4;
            default: throw new IllegalArgumentException("Unexpected quarter: " + quarter);
        }
    }


}
