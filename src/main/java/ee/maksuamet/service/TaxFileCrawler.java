package ee.maksuamet.service;

import ee.maksuamet.domain.Company;
import ee.maksuamet.domain.Quarter;
import ee.maksuamet.helpers.Helpers;
import ee.maksuamet.repository.CompanyRepository;
import ee.maksuamet.repository.QuarterRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class TaxFileCrawler {

    private static final Logger logger = LoggerFactory.getLogger(TaxFileCrawler.class);
    private final Helpers helpers = new Helpers();
    private static final String TARGET_URL = "https://www.emta.ee/ariklient/amet-uudised-ja-kontakt/uudised-pressiinfo-statistika/statistika-ja-avaandmed";
    private static final String CSV_DIR = "src/main/resources/csv_files/";
//    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("tasutud_maksud_(\\d{4})_(i{1,3}|iv)_kvartal\\.csv", Pattern.CASE_INSENSITIVE);
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("tasutud_maksud_2024_ii_kvartal\\.csv", Pattern.CASE_INSENSITIVE);

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private QuarterRepository quarterRepository;


    public void checkAndDownloadFiles() {
        try {
            Document document = Jsoup.connect(TARGET_URL).get();
            Elements links = document.select("a[href$=.csv]");

            for (Element link : links) {
                String fileUrl = link.absUrl("href");
                String fileName = extractFileName(fileUrl);

                if (FILE_NAME_PATTERN.matcher(fileName).matches()) {
                    // Check if the file is already in the database
                    if (quarterRepository.findByFileName(fileName).isEmpty()) {
                        Path filePath = Paths.get(CSV_DIR, fileName);
                        downloadFile(fileUrl, filePath);

                        // Save information about the quarter in the database
                        Quarter newQuarter = createAndSaveQuarter(fileName);

                        // Read CSV and save to database with reference to the quarter
                        readCsvAndSaveToDatabase(filePath, newQuarter);
                        logger.info("File: {} saved to database", filePath.getFileName());

                        // Delete the CSV file after successfully reading it to the database
                        try {
                            Files.delete(filePath);
                            logger.info("Deleted file: {}", filePath.getFileName());
                        } catch (IOException e) {
                            logger.error("Error deleting file {}: {}", filePath.getFileName(), e.getMessage(), e);
                        }
                    } else {
                        logger.info("Quarter with file name '{}' already exists in the database. Skipping download.", fileName);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error occurred while checking and downloading files: {}", e.getMessage(), e);
        }
    }

    private Quarter createAndSaveQuarter(String fileName) {
        String[] parts = fileName.split("_");
        Quarter newQuarter = new Quarter();
        newQuarter.setYear(parts[2]);
        newQuarter.setQuarter(helpers.quarterToInt(parts[3].toUpperCase()));
        newQuarter.setFileName(fileName);
        return quarterRepository.save(newQuarter);
    }

    private String extractFileName(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    private void downloadFile(String fileUrl, Path filePath) throws IOException {
        try (InputStream in = new URL(fileUrl).openStream();
             FileOutputStream out = new FileOutputStream(filePath.toFile())) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            logger.info("Downloaded: {}", filePath.getFileName());
        }
    }

    private void readCsvAndSaveToDatabase(Path filePath, Quarter quarter) {
        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip the header line
                    continue;
                }
                Company company = parseCompanyFromLine(line);
                company.setQuarter(quarter); // Set the quarter reference before saving
                companyRepository.save(company); // Save to the database
            }
        } catch (IOException e) {
            logger.error("Error reading CSV file: {}", e.getMessage(), e);
        }
    }

    private Company parseCompanyFromLine(String line) {
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

    private String[] parseCsvLine(String line) {
        // Same CSV parsing logic as before
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

