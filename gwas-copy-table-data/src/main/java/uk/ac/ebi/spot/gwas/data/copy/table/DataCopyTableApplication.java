package uk.ac.ebi.spot.gwas.data.copy.table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "uk.ac.ebi.spot.gwas")
public class DataCopyTableApplication {

    private final static Logger log = LoggerFactory.getLogger(DataCopyTableApplication.class);

    public static void main(String[] args) {
        log.info("Data Copy started");
        ApplicationContext ctx = new SpringApplicationBuilder(DataCopyTableApplication.class).web(WebApplicationType.NONE).run(args);
        log.info("Data Copy finished");
        SpringApplication.exit(ctx);

    }
}
