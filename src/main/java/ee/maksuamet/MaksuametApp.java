package ee.maksuamet;

import ee.maksuamet.repository.CompanyRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class MaksuametApp {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(MaksuametApp.class, args);
    }
}