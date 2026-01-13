package uk.ac.ebi.spot.gwas.submission.nextflow;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = "uk.ac.ebi.spot.gwas")
@Slf4j
public class NextFlowImportApplication {
    public static void main(String[] args) {
        log.info("Starting Next Flow Import Application");
        ApplicationContext ctx = new SpringApplicationBuilder(NextFlowImportApplication.class).web(WebApplicationType.NONE).run(args);
        log.info("Finished Next Flow Import Application");
        SpringApplication.exit(ctx);
    }


}
