package uk.ac.ebi.spot.gwas.data.copy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "uk.ac.ebi.spot.gwas")
@EnableAsync
public class DataCopyApplication {

    private final static Logger log = LoggerFactory.getLogger(DataCopyApplication.class);

    public static void main(String[] args) {
        log.info("Data Copy started");
        ApplicationContext ctx = new SpringApplicationBuilder(DataCopyApplication.class).web(WebApplicationType.NONE).run(args);
        log.info("Data Copy finished");
        SpringApplication.exit(ctx);

    }
}
