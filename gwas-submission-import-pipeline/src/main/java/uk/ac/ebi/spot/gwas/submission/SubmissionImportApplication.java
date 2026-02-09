package uk.ac.ebi.spot.gwas.submission;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = "uk.ac.ebi.spot.gwas")
@EntityScan(basePackages = {"uk.ac.ebi.spot.gwas.model"})
@Slf4j
public class SubmissionImportApplication {

    public static void main(String[] args) {
        log.info("SubmissionImportApplication started");
        ApplicationContext ctx = new SpringApplicationBuilder(SubmissionImportApplication.class).web(WebApplicationType.NONE).run(args);;
        log.info("SubmissionImportApplication finished");
        SpringApplication.exit(ctx);
    }
}
