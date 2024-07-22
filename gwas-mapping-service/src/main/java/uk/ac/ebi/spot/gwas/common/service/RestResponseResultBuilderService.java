package uk.ac.ebi.spot.gwas.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.common.config.AppConfig;
import uk.ac.ebi.spot.gwas.common.constant.Type;
import uk.ac.ebi.spot.gwas.mapping.dto.RestResponseResult;

@Service
public class RestResponseResultBuilderService {

    private final AppConfig config;
    private final EnsemblRestcallHistoryService historyService;

    private final ObjectMapper mapper = new ObjectMapper();

    public RestResponseResultBuilderService(AppConfig config, EnsemblRestcallHistoryService historyService) {
        this.config = config;
        this.historyService = historyService;
    }

    public void  buildResponseResult(String uri, String param , String type,
                                           ResponseEntity<String> entity) {
        RestResponseResult restResponseResult = new RestResponseResult();
        if(entity.getStatusCodeValue() == HttpStatus.OK.value()) {
            restResponseResult.setRestResult(entity.getBody());
        }else {
            restResponseResult.setError(entity.getBody());
        }
        restResponseResult.setStatus(entity.getStatusCodeValue());
        if(entity.getStatusCodeValue() == HttpStatus.BAD_GATEWAY.value() ||
                entity.getStatusCodeValue() == HttpStatus.SERVICE_UNAVAILABLE.value() ||
                entity.getStatusCodeValue() == HttpStatus.GATEWAY_TIMEOUT.value()) {
            restResponseResult.setError("Ensembl is down. Contact the admin.");
        }
        restResponseResult.setUrl(uri);
        historyService.create(restResponseResult, type, param, config.getERelease());
    }
}
