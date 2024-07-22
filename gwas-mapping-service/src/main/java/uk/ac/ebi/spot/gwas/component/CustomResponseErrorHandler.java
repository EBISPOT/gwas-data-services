package uk.ac.ebi.spot.gwas.component;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import uk.ac.ebi.spot.gwas.exception.EnsemblRestClientException;

import java.io.IOException;

/**
 * Created by cinzia on 10/04/2017.
 */
@Component

public class CustomResponseErrorHandler implements ResponseErrorHandler {

    private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

    public boolean hasError(ClientHttpResponse response) throws IOException {
        return errorHandler.hasError(response);
    }

    public void handleError(ClientHttpResponse response) throws IOException {
        try {
            errorHandler.handleError(response);
        } catch (RestClientException restClientException) {
            throw new EnsemblRestClientException("Custom RestTemplate", restClientException,response);
        }
    }
}