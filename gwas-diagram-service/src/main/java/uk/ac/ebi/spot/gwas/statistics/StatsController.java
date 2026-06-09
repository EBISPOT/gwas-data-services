package uk.ac.ebi.spot.gwas.statistics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import uk.ac.ebi.spot.gwas.solr.SolrService;
import uk.ac.ebi.spot.gwas.solr.dto.Doc;
import uk.ac.ebi.spot.gwas.solr.dto.SolrData;

import java.util.*;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class StatsController {


    @Value("${url.server}")
    private String serverUrl;
    private final SolrService solrService;

    private final ObjectMapper mapper = new ObjectMapper();

    public StatsController(SolrService solrService) {
        this.solrService = solrService;
    }

    @GetMapping("/stats")
    public Object getStats() throws JsonProcessingException {
        String uri = String.format("%s/select?q=type:%s&wt=json&indent=true", serverUrl, "statistics");
        SolrData solrData = mapper.convertValue(solrService.getRequest(uri).getBody(), new TypeReference<SolrData>() {});
        List<Doc> docs = solrData.getResponse().getDocs();
        String data = docs.get(0).getData().replace("=", ":");
        return mapper.readValue(data, Map.class);
    }



}


