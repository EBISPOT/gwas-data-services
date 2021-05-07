package uk.ac.ebi.spot.gwas.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfiguration {

    static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateConfiguration.class);

    private static final int HTTP_CLIENT_RETRY_COUNT = 3;
    private static final int MAXIMUM_TOTAL_CONNECTION = 10;
    private static final int MAXIMUM_CONNECTION_PER_ROUTE = 5;
    private static final int CONNECTION_VALIDATE_AFTER_INACTIVITY_MS = 10 * 1000;

    public RestTemplateConfiguration() {
        // Hide implicit one
    }

    @Bean
    public static RestTemplate restTemplate() {

        int connectionTimeoutMs = 1800000;
        int readTimeoutMs = 1800000;
        ObjectMapper objectMapper = new ObjectMapper();

        HttpClientBuilder clientBuilder = HttpClients.custom();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

        // Set the maximum number of total open connections.
        connectionManager.setMaxTotal(MAXIMUM_TOTAL_CONNECTION);
        // Set the maximum number of concurrent connections per route, which is 2 by default.
        connectionManager.setDefaultMaxPerRoute(MAXIMUM_CONNECTION_PER_ROUTE);
        connectionManager.setValidateAfterInactivity(CONNECTION_VALIDATE_AFTER_INACTIVITY_MS);
        clientBuilder.setConnectionManager(connectionManager);
        clientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(HTTP_CLIENT_RETRY_COUNT, true, new ArrayList<>()) {

            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                LOGGER.info("Retry request, execution count: {}, exception: {}", executionCount, exception);
                return super.retryRequest(exception, executionCount, context);
            }

        });

        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(clientBuilder.build());
        httpRequestFactory.setConnectTimeout(connectionTimeoutMs);
        httpRequestFactory.setConnectionRequestTimeout(readTimeoutMs);
        httpRequestFactory.setReadTimeout(readTimeoutMs);

        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
        interceptors.add(new LoggingRequestInterceptor());
        restTemplate.setInterceptors(interceptors);
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(httpRequestFactory));

        MappingJackson2HttpMessageConverter messageConverter = restTemplate.getMessageConverters().stream().filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .map(MappingJackson2HttpMessageConverter.class::cast).findFirst().orElseThrow(() -> new RuntimeException("MappingJackson2HttpMessageConverter not found"));
        messageConverter.setObjectMapper(objectMapper);

        restTemplate.getMessageConverters().stream().filter(StringHttpMessageConverter.class::isInstance).map(StringHttpMessageConverter.class::cast).forEach(a -> {
            a.setWriteAcceptCharset(false);
            a.setDefaultCharset(StandardCharsets.UTF_8);
        });
        return restTemplate;
    }
}

class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        traceRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        traceResponse(response);
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) throws IOException {
        LOGGER.debug("===========================request begin================================================");
        LOGGER.debug("URI         : {}", request.getURI());
        LOGGER.debug("Method      : {}", request.getMethod());
        LOGGER.debug("Headers     : {}", request.getHeaders());
        LOGGER.debug("Request body: {}", new String(body, "UTF-8"));
        LOGGER.debug("==========================request end================================================");
    }

    private void traceResponse(ClientHttpResponse response) throws IOException {
        StringBuilder inputStringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
        String line = bufferedReader.readLine();
        while (line != null) {
            inputStringBuilder.append(line);
            inputStringBuilder.append('\n');
            line = bufferedReader.readLine();
        }
        LOGGER.debug("============================response begin==========================================");
        LOGGER.debug("Status code  : {}", response.getStatusCode());
        LOGGER.debug("Status text  : {}", response.getStatusText());
        LOGGER.debug("Headers      : {}", response.getHeaders());
        LOGGER.debug("Response body: {}", inputStringBuilder.toString());
        LOGGER.debug("=======================response end=================================================");
    }
}
