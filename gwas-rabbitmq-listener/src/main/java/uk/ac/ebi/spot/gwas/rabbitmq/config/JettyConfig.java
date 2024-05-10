
package uk.ac.ebi.spot.gwas.rabbitmq.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.spot.gwas.deposition.config.SystemConfigProperties;

@Configuration
public class JettyConfig {


    @Value("${server.port}")
    private String serverPort;

    @Bean
    public ServletWebServerFactory servletContainer() {
        JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
        int port = Integer.parseInt(serverPort);
        factory.setPort(port);
        return factory;
    }
}


