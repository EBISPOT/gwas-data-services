package uk.ac.ebi.spot.gwas.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.ebi.spot.gwas.config.RestAPIConfiguration;
import uk.ac.ebi.spot.gwas.ols.OLSTermApiResponse;
import uk.ac.ebi.spot.gwas.service.RestInteractionService;

import java.util.Optional;

@Slf4j
@Service
public class RestInteractionServiceImpl implements RestInteractionService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    RestAPIConfiguration restAPIConfiguration;

    public OLSTermApiResponse callOlsRestAPI(String uri, String efoId, Boolean next) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
        ResponseEntity<OLSTermApiResponse> responseEntity = null;
        MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
        paramsMap.add("id",efoId);
        paramsMap.add("size","500");
        String olsUri = "";
        if(!next) {
             olsUri = UriComponentsBuilder.fromHttpUrl(uri).queryParams(paramsMap).build().toUriString();
        } else {
            olsUri = uri;
        }
        log.info("The OLS API call uri is ->"+olsUri);
        try {
            responseEntity = restTemplate.exchange(olsUri, HttpMethod.GET, httpEntity, new ParameterizedTypeReference
                    <OLSTermApiResponse>() {
            });
        }catch(Exception ex){
            log.error("Exception in Rest API call"+ex.getMessage(),ex);
        }
        //log.info("responseEntity status code"+responseEntity.getStatusCodeValue());
        //log.info("responseEntity Body"+responseEntity.getBody());

        return Optional.ofNullable(responseEntity).map(ResponseEntity::getBody).orElse(null);
    }

}
