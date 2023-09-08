package uk.ac.ebi.spot.gwas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MappingPipelineApplication {

    private final static Logger log = LoggerFactory.getLogger(MappingPipelineApplication.class);

    public static void main(String[] args) {
        log.info("Mapping Pipeline started");
        ApplicationContext ctx = new SpringApplicationBuilder(MappingPipelineApplication.class).web(WebApplicationType.NONE).run(args);
        log.info("Mapping Pipeline finished");
        SpringApplication.exit(ctx);

    }
}
