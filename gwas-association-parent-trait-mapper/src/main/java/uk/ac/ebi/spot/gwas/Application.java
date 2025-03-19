package uk.ac.ebi.spot.gwas;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.SpringApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = "uk.ac.ebi.spot.gwas")
public class Application {

    public static void main(String[] args) {
        log.info("Association Parent Trait Mapper started");
        ApplicationContext ctx = new SpringApplicationBuilder(Application.class).web(WebApplicationType.NONE).run(args);
        log.info("Association Parent Trait Mapper ended");
        SpringApplication.exit(ctx);
    }
}
