
package uk.ac.ebi.spot.gwas.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import uk.ac.ebi.spot.gwas.exception.RestTemplateException;

import java.io.IOException;

@Slf4j
@Configuration
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {


    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        return (clientHttpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR ||
                clientHttpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        if(clientHttpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            log.error("Inside Rest Template Client Error");
            throw new RestTemplateException("Error in retreiving details from Ensembl API");
        }
        if(clientHttpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            log.error("Inside Rest Template Server Error");
            throw new RestTemplateException("Error in retreiving details from Ensembl API");
        }
    }
}

