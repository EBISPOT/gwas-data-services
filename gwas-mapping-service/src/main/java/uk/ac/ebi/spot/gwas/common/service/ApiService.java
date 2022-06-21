package uk.ac.ebi.spot.gwas.common.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.gwas.common.config.AppConfig;
import uk.ac.ebi.spot.gwas.common.constant.Uri;
import uk.ac.ebi.spot.gwas.gene_symbol.GeneSymbol;
import uk.ac.ebi.spot.gwas.variation.Variant;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Data
@Service
public class ApiService {

    private Integer ensemblCount = 0;
    private ObjectMapper mapper = new ObjectMapper();
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppConfig config;

    public void setEnsemblCount(Integer ensemblCount) {
        this.ensemblCount += ensemblCount;
    }

    @Async("asyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Map<String, Variant>> variationPost(List<String> snpRsIds) {
        List<Object> cleaned = snpRsIds.stream().map(String::trim).collect(Collectors.toList());
        log.info("Start getting next {} snp rsIds from Ensembl", cleaned.size());
        Object response = postRequest(Collections.singletonMap("ids", cleaned), String.format("%s/%s", config.getServer(), Uri.VARIATION));
        Map<String, Variant> variantMap = mapper.convertValue(response, new TypeReference<Map<String, Variant>>() {});

        setEnsemblCount(cleaned.size());
        log.info("Finished getting {} snp rsIds from Ensembl", getEnsemblCount());
        return CompletableFuture.completedFuture(variantMap);
    }

    @Async("asyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Map<String, GeneSymbol>> geneSymbolPost(List<String> reportedGenes) {
        log.info("Start getting next {} reported geneIds from Ensembl", reportedGenes.size());
        Object response = postRequest(Collections.singletonMap("symbols", reportedGenes),
                                      String.format("%s/%s", config.getServer(), Uri.REPORTED_GENES));
        Map<String, GeneSymbol> geneMap = mapper.convertValue(response, new TypeReference<Map<String, GeneSymbol>>() {});

        setEnsemblCount(reportedGenes.size());
        log.info("Finished getting {} reported geneIds from Ensembl", getEnsemblCount());
        return CompletableFuture.completedFuture(geneMap);
    }

    public Object postRequest(Map<String, Object> request, String uri) {
        Object response = null;
        try {
            response = restTemplate.postForObject(new URI(uri), request, Object.class);
        } catch (URISyntaxException | HttpStatusCodeException e) {
            log.error("Error: {} for SNP RsIds: {} retrying ...", e.getMessage(), request);
        }
        return response;
    }

    public Optional<ResponseEntity<Object>> getRequest(String uri) {
        log.info("Calling: {}", uri);
        ResponseEntity<Object> response = null;
        try {
            response = restTemplate.getForEntity(uri, Object.class);
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                log.warn("warning: too many request {} retrying ...", uri);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                }
                return this.getRequest(uri);
            }else{
                response = new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
            }
        }
        return Optional.of(response);
    }
}
