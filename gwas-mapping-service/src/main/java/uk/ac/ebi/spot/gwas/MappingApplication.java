package uk.ac.ebi.spot.gwas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class MappingApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = new SpringApplicationBuilder(MappingApplication.class).web(WebApplicationType.NONE).run(args);
        SpringApplication.exit(ctx);
       // SpringApplication.run(MappingApplication.class, args);
    }

}
