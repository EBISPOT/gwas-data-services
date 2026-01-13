package uk.ac.ebi.spot.gwas.submission;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "uk.ac.ebi.spot.gwas")
@EntityScan(basePackages = {"uk.ac.ebi.spot.gwas.model"})
@Slf4j
public class SubmissionImportApplication {

    public static void main(String[] args) {
        log.info("SubmissionImportApplication started");
        SpringApplication.run(SubmissionImportApplication.class, args);
        log.info("SubmissionImportApplication finished");

    }
}
