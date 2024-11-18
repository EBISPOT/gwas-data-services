package uk.ac.ebi.spot.gwas.common.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.gwas.assembly_info.AssemblyInfo;
import uk.ac.ebi.spot.gwas.common.config.AppConfig;
import uk.ac.ebi.spot.gwas.common.constant.Uri;
import uk.ac.ebi.spot.gwas.exception.EnsemblRestClientException;
import uk.ac.ebi.spot.gwas.gene_symbol.GeneSymbol;
import uk.ac.ebi.spot.gwas.overlap_gene.OverlapGene;
import uk.ac.ebi.spot.gwas.overlap_region.OverlapRegion;
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

    public ResponseEntity<String> getRequestBody(String uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> out = null;
        List<MediaType> mediaTypes = new ArrayList<MediaType>();
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.ALL);
        headers.setAccept(mediaTypes);
        //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON,MediaType.TEXT_HTML));

        HttpEntity<Object> entity = new HttpEntity<Object>(headers);
        try {
            out = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        }catch(HttpStatusCodeException e) {
            getLog().debug("EnsemblRestClientException");
            out = new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        }
        catch (Exception e) {
            getLog().debug("Exception not managed");
        }
        return out;

    }
    public Optional<ResponseEntity<Variant>> getRequestVariant(String uri) {
        log.info("Calling: {}", uri);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Variant> out = null;
        List<MediaType> mediaTypes = new ArrayList<MediaType>();
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.ALL);
        headers.setAccept(mediaTypes);
        ResponseEntity<String> response = null;
        HttpEntity<Object> entity = new HttpEntity<Object>(headers);
        try {
            //response = restTemplate.getForEntity(uri, Object.class);
            out = restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference
                    <Variant>() {
            });
            log.info("Ressponse body in getRequest() {}",out.getBody());
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                log.warn("warning: too many request {} retrying ...", uri);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                }
                return this.getRequestVariant(uri);
            }
            else if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                log.debug("Inside Bad request block");
                log.debug("Variant error is {}", e.getResponseBodyAsString());
                out = new ResponseEntity<>(new Variant(e.getResponseBodyAsString()), e.getStatusCode());
            }
            else{
                out = new ResponseEntity<>(new Variant(e.getResponseBodyAsString()), e.getStatusCode());
            }
        }
        return Optional.of(out);
    }

    public Optional<ResponseEntity<List<OverlapRegion>>> getRequestOverlapRegion(String uri) {
        log.info("Calling: {}", uri);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<List<OverlapRegion>> out = null;
        List<MediaType> mediaTypes = new ArrayList<MediaType>();
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.ALL);
        headers.setAccept(mediaTypes);
        ResponseEntity<String> response = null;
        HttpEntity<Object> entity = new HttpEntity<Object>(headers);
        try {
            //response = restTemplate.getForEntity(uri, Object.class);
            out = restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference
                    <List<OverlapRegion>>() {
            });
            log.info("Ressponse body in getRequest() {}",out.getBody());
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                log.warn("warning: too many request {} retrying ...", uri);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                }
                return this.getRequestOverlapRegion(uri);
            }
            else if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                log.debug("Inside Bad request block");
                log.debug("OverlapRegion error is {}", e.getResponseBodyAsString());
                OverlapRegion overlapRegion = new OverlapRegion(e.getResponseBodyAsString());
                out = new ResponseEntity<>(Collections.singletonList(overlapRegion) , e.getStatusCode());
            }
            else{
                OverlapRegion overlapRegion = new OverlapRegion(e.getResponseBodyAsString());
                out = new ResponseEntity<>(Collections.singletonList(overlapRegion), e.getStatusCode());
            }
        }
        return Optional.of(out);
    }


    public Optional<ResponseEntity<List<OverlapGene>>> getRequestOverlapGene(String uri) {
        log.info("Calling: {}", uri);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<List<OverlapGene>> out = null;
        List<MediaType> mediaTypes = new ArrayList<MediaType>();
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.ALL);
        headers.setAccept(mediaTypes);
        ResponseEntity<String> response = null;
        HttpEntity<Object> entity = new HttpEntity<Object>(headers);
        try {
            //response = restTemplate.getForEntity(uri, Object.class);
            out = restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference
                    <List<OverlapGene>>() {
            });
            log.info("Ressponse body in getRequest() {}",out.getBody());
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                log.warn("warning: too many request {} retrying ...", uri);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                }
                return this.getRequestOverlapGene(uri);
            }
            else if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                log.debug("Inside Bad request block");
                log.error("OverlapGene error is {}", e.getResponseBodyAsString());
                OverlapGene overlapGene = new OverlapGene(e.getResponseBodyAsString());
                out = new ResponseEntity<>(Collections.singletonList(overlapGene) , e.getStatusCode());
            }
            else{
                OverlapGene overlapGene = new OverlapGene(e.getResponseBodyAsString());
                out = new ResponseEntity<>(Collections.singletonList(overlapGene) , e.getStatusCode());
            }
        }
        return Optional.of(out);
    }

    public Optional<ResponseEntity<GeneSymbol>> getRequestGeneSymbol(String uri) {
        log.info("Calling: {}", uri);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<GeneSymbol> out = null;
        List<MediaType> mediaTypes = new ArrayList<MediaType>();
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.ALL);
        headers.setAccept(mediaTypes);
        ResponseEntity<String> response = null;
        HttpEntity<Object> entity = new HttpEntity<Object>(headers);
        try {
            //response = restTemplate.getForEntity(uri, Object.class);
            out = restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference
                    <GeneSymbol>() {
            });
            log.info("Ressponse body in getRequest() {}",out.getBody());
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                log.warn("warning: too many request {} retrying ...", uri);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                }
                return this.getRequestGeneSymbol(uri);
            }
            else if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                log.debug("Inside Bad request block");
                log.error("GeneSymbol error is {}", e.getResponseBodyAsString());
                GeneSymbol geneSymbol = new GeneSymbol(e.getResponseBodyAsString());
                out = new ResponseEntity<>(geneSymbol , e.getStatusCode());
            }
            else{
                GeneSymbol geneSymbol = new GeneSymbol(e.getResponseBodyAsString());
                out = new ResponseEntity<>(geneSymbol , e.getStatusCode());
            }
        }
        return Optional.of(out);
    }

    public Optional<ResponseEntity<AssemblyInfo>> getRequestAssemblyInfo(String uri) {
        log.info("Calling: {}", uri);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<AssemblyInfo> out = null;
        List<MediaType> mediaTypes = new ArrayList<MediaType>();
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.ALL);
        headers.setAccept(mediaTypes);
        ResponseEntity<String> response = null;
        HttpEntity<Object> entity = new HttpEntity<Object>(headers);
        try {
            //response = restTemplate.getForEntity(uri, Object.class);
            out = restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference
                    <AssemblyInfo>() {
            });
            log.info("Ressponse body in getRequest() {}",out.getBody());
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                log.warn("warning: too many request {} retrying ...", uri);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                }
                return this.getRequestAssemblyInfo(uri);
            }
            else{
                out = new ResponseEntity<>(new AssemblyInfo(), e.getStatusCode());
            }
        }
        return Optional.of(out);
    }


}
