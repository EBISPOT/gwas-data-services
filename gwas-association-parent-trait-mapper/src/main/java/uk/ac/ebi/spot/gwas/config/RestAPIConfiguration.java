package uk.ac.ebi.spot.gwas.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RestAPIConfiguration {

    @Value("${ols-api.endpoint:#{NULL}}")
    private String olaApiEndpoint;

}
