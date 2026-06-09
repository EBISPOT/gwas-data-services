package uk.ac.ebi.spot.gwas.chromosome;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Chromosome", description = "Endpoints for chromosome-related data retrieval.")
@RestController
public class ChromosomeController {

    @Value("${url.server}")
    private String serverUrl;
    private final SolrService solrService;

    private final ObjectMapper mapper = new ObjectMapper();

    public ChromosomeController(SolrService solrService) {
        this.solrService = solrService;
    }


    @Operation(summary = "Get Solr data for a specific chromosome", description = "Fetches chromosome data from Solr, optionally filtered by a parent trait.")
    @GetMapping("/chromosomes/{chrId}")
    public Object getSolrData(@PathVariable String chrId, @RequestParam(required = false) String parent) {

        String uri = String.format("%s/select?q=chromosome:%s  AND facet:full_data&rows=2147483647&wt=json&indent=true", serverUrl, chrId);
        if (Optional.ofNullable(parent).isPresent()) {
            parent = parent.trim().toLowerCase();
            uri = String.format("%s/select?q=chromosome:%s AND parent_trait:\"%s\"&rows=2147483647&wt=json&indent=true", serverUrl, chrId, parent);
        }

        log.info(uri);
        SolrData solrData = mapper.convertValue(solrService.getRequest(uri).getBody(), new TypeReference<SolrData>() {});

        List<Doc> docs = mapper.convertValue(solrData.getResponse().getDocs(), new TypeReference<List<Doc>>() {});

        List<ChromosomeDto> chromosomeDtos = new ArrayList<>();
        docs.forEach(doc -> {
            Map<String, String> categories = new HashMap<>();
            try {
                categories = mapper.readValue(doc.getCategory(), new TypeReference<Map<String, String>>() {});
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }
            chromosomeDtos.add(ChromosomeDto.builder()
                    .id(doc.getId())
                    .region(doc.getCytogeneticRegion())
                    .chromosome(doc.getChromosome())
                    .facet(doc.getFacet())
                    .traits(doc.getTraits())
                    .parentTrait(doc.getParentTrait())
                    .categories(categories)
                    .build());
        });

        return chromosomeDtos;
    }

}


