package uk.ac.ebi.spot.gwas.rabbitmq.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BackendEmailConfig {


    @Value("${email-config.to-address}")
    private String toAddress;

    public String getToAddress() {
        return toAddress;
    }
}
