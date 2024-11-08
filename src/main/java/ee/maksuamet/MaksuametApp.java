package ee.maksuamet;

import ee.maksuamet.repository.CompanyRepository;
import ee.maksuamet.service.TaxFileCrawler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class MaksuametApp {
    public static void main(String[] args) {

        // Start the Spring application context and get all beans initialized
        ApplicationContext context = SpringApplication.run(MaksuametApp.class, args);

        // Get the TaxFileCrawler bean from the Spring context
        TaxFileCrawler crawler = context.getBean(TaxFileCrawler.class);

        // Now call the checkAndDownloadFiles method on the properly initialized bean
        crawler.checkAndDownloadFiles();
    }
}