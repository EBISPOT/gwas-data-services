package uk.ac.ebi.spot.gwas.ontology;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.gwas.ontology.dto.ChildTrait;
import uk.ac.ebi.spot.gwas.ontology.dto.ParentTrait;
import uk.ac.ebi.spot.gwas.ontology.ols_payload.OLSTermApiDoc;

import java.util.*;

@Slf4j
@Service
public class OlsService {

    @Value("${url.efo}")
    private String efoUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ParentTraitService parentTraitService;

    private final ObjectMapper mapper = new ObjectMapper();

    public List<ChildTrait> getAllChildrenForThisParentTrait(ParentTrait parentTrait) {
        String uri = String.format(efoUrl, parentTrait.getEfoId());
        List<ChildTrait> childTraits = mapper.convertValue(this.getRequest(uri).getBody(), new TypeReference<List<ChildTrait>>() {});
        childTraits.add(
                ChildTrait.builder()
                        .key(parentTrait.getEfoId())
                        .label(parentTrait.getLabel())
                        .build()
        );
        return childTraits;
    }

    public Map<String, String> callOlsRestAPI(String url, String efoId, String parentClass) {
        Map<String, String> olsData = new HashMap<>();
        url = String.format(url, efoId);
        ResponseEntity<OLSTermApiDoc> response = restTemplate.getForEntity(url, OLSTermApiDoc.class);
        OLSTermApiDoc olsDoc = response.getBody();

        log.info("Calling: {}", url);
        if (olsDoc != null) {
            olsDoc.getEmbedded().getTerms().forEach(olsTerm -> olsData.put(olsTerm.getLabel(), parentClass));

            if (olsDoc.getLinks().getNext() != null) {
                olsData.putAll(this.callOlsRestAPI(olsDoc.getLinks().getNext().getHref(), efoId, parentClass));
            }
        }

        return olsData;
    }

    public Map<String, String> getChildrenTraits() {
        Map<String, String> childParentMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (ParentTrait parentTrait : parentTraitService.getTraits()) {
            List<ChildTrait> childTraits = this.getAllChildrenForThisParentTrait(parentTrait);
            childTraits.forEach(childTrait -> childParentMap.put(childTrait.getLabel(), parentTrait.getLabel()));
        }
        return childParentMap;
    }

    public ResponseEntity<Object> getRequest(String uri) {
        ResponseEntity<Object> response = null;
        try {
            response = restTemplate.getForEntity(uri, Object.class);
        } catch (HttpStatusCodeException e) {
            response = new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        }
        return response;
    }
}
