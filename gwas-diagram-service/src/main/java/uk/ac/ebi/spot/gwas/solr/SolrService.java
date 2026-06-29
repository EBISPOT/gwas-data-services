package uk.ac.ebi.spot.gwas.solr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.gwas.solr.dto.Doc;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Slf4j
@Service
public class SolrService {

    @Value("${url.solr}")
    private String url;

    @Value("${url.server}")
    private String server;

    @Autowired
    private RestTemplate restTemplate;

    public Object cleanSolr() {
        Map<String, Object> deleteMap = new HashMap<>();
        Map<String, Object> solrMap = new HashMap<>();
        deleteMap.put("query", "*:*");
        solrMap.put("delete", deleteMap);
        log.info("Solr content deleted successfully");
        return this.postRequest(solrMap, String.format("%s/update?commit=true", server));
    }

    public void initSetUp() {
        String core = "gwas-diagram";
        log.info("Solr core deleted successfully {}", this.deleteCore(core));
        log.info("Solr core created successfully {}", this.createCore(core));

        addFieldToConfigXml(core, "traits", false, true);
        addFieldToConfigXml(core, "region", false, false);
        addFieldToConfigXml(core, "chromosome", false, false);
        addFieldToConfigXml(core, "facet", false, false);
        log.info("Config file successfully updated");
    }

    public Object save(Doc doc) {
        Map<String, Object> parentMap = new HashMap<>();
        Map<String, Object> solrMap = new HashMap<>();

        parentMap.put("doc", doc);
        solrMap.put("add", parentMap);

       // Operation.prettyPrint(solrMap);
        return this.postRequest(solrMap, url);
    }

    public Object createCore(String core) {
        String BASE_URL = "http://gwas-snoopy.ebi.ac.uk:8983/solr";
        String url = String.format("%s/admin/cores?action=CREATE&name=%s&instanceDir=%s&configSet=_default", BASE_URL, core, core);
        return this.getRequest(url);
    }

    public Object deleteCore(String core) {
        String BASE_URL = "http://gwas-snoopy.ebi.ac.uk:8983/solr";
        String url = String.format("%s/admin/cores?action=UNLOAD&core=%s&deleteIndex=true&deleteDataDir=true&deleteInstanceDir=true", BASE_URL, core);
        return this.getRequest(url);
    }

    public Object addFieldToConfigXml(String core, String name, boolean indexed, boolean multivalued) {
        Map<String, Object> fieldMap = new HashMap<>();
        Map<String, Object> solrMap = new HashMap<>();

        fieldMap.put("name", name);
        fieldMap.put("type", "string");
        fieldMap.put("indexed", indexed);
        fieldMap.put("stored", true);
        fieldMap.put("multivalued", multivalued);
        solrMap.put("add-field", fieldMap);
        return this.postRequest(solrMap, String.format("http://gwas-snoopy.ebi.ac.uk:8983/api/cores/%s/schema", core));
    }

    public Object postRequest(Map<String, Object> request, String uri) {
        Object response = null;
        try {
            response = restTemplate.postForObject(new URI(uri), request, String.class);
        } catch (URISyntaxException | HttpStatusCodeException e) {
            log.error("Error: {} for SNP ... {} retrying ...", e.getMessage(), request);
        }
        return response;
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
