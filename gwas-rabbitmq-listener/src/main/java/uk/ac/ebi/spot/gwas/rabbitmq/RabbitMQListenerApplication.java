package uk.ac.ebi.spot.gwas.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication(scanBasePackages = "uk.ac.ebi.spot.gwas")
@EntityScan(basePackages = {"uk.ac.ebi.spot.gwas.model"})
public class RabbitMQListenerApplication implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQListenerApplication.class);

    public static void main(String[] args) {

        log.info("RabbitMQListener started");
        SpringApplication.run(RabbitMQListenerApplication.class, args);
        //ApplicationContext ctx = new SpringApplicationBuilder(RabbitMQListenerApplication.class).web(WebApplicationType.NONE).run(args);
        log.info("RabbitMQListener Ended");
        //SpringApplication.exit(ctx);
    }
}
