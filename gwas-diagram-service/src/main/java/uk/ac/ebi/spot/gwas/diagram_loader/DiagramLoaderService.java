package uk.ac.ebi.spot.gwas.diagram_loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.association.AssociationRepository;
import uk.ac.ebi.spot.gwas.ontology.ParentTraitService;
import uk.ac.ebi.spot.gwas.ontology.dto.ParentTrait;
import uk.ac.ebi.spot.gwas.ontology.OlsService;
import uk.ac.ebi.spot.gwas.solr.SolrService;
import uk.ac.ebi.spot.gwas.solr.dto.Doc;
import uk.ac.ebi.spot.gwas.solr.dto.Operation;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class DiagramLoaderService {

    @Autowired
    private SolrService solrService;

    @Autowired
    private OlsService olsService;

    @Autowired
    private AssociationRepository associationRepository;

    @Value("${url.ols}")
    private String olsUrl;

    Map<String, Integer> parentChildCounter = new HashMap<>();
    Map<String, Set<String>> parentChildHolder = new HashMap<>();
    private Map<String, String> traitMap;
    private Map<String, String> childParentMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private Map<String, String> diseaseBranch;
    private Map<String, String> measurementBranch;

    private final ObjectMapper mapper = new ObjectMapper();
    private final List<String> chromosomes = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "X", "Y");


    @Autowired
    private ParentTraitService parentTraitService;

    public Object indexFullData() throws JSONException, IOException {

        parentTraitService.getTraits().forEach(parentTrait -> parentChildCounter.put(parentTrait.getLabel(), 0));
        parentChildCounter.put("other measurement", 0);
        parentChildCounter.put("other disease", 0);
        parentChildCounter.put("other trait", 0);

        for (ParentTrait parentTrait : parentTraitService.getTraits()) {
            parentChildHolder.put(parentTrait.getLabel(), new HashSet<>());
        }
        parentChildHolder.put("other measurement", new HashSet<>());
        parentChildHolder.put("other disease", new HashSet<>());
        parentChildHolder.put("other trait", new HashSet<>());

        log.info("Start getting child Trait for all Labelled Parent category");
        this.childParentMap = olsService.getChildrenTraits(); // for childParentMap, the key is the childTrait, & value is the parent
        log.info("Finish getting child Trait for all Labelled Parent category");

        // Get disease branch from OLS
        log.info("Start getting disease branch from OLS");
        this.diseaseBranch = olsService.callOlsRestAPI(olsUrl, "MONDO_0000001", "other disease");
        log.info("Finished getting disease branch from OLS");

        log.info("Start getting measurement branch from OLS");
        this.measurementBranch = olsService.callOlsRestAPI(olsUrl, "EFO_0001444", "other measurement");
        log.info("Finished getting measurement branch from OLS");


        // For chromosomes 1 get the regions:
        for (String chromosome : chromosomes) {
            List<Map<String, String>> mapsOfRegionsForAChromosome = associationRepository.findRegionsByChromosomeName(chromosome);

            for (Map<String, String> oneMapOfRegion : mapsOfRegionsForAChromosome) {// chromosome / region / tr... / facet / parent

                Doc doc = Doc.builder()
                        .cytogeneticRegion(oneMapOfRegion.get("REGION_NAME"))
                        .chromosome(chromosome)
                        .traits(associationRepository.findByChromosomeNameAndRegion(chromosome, String.valueOf(oneMapOfRegion.get("REGION_NAME"))))
                        .facet("full_data")
                        .id(Operation.generateUUId())
                        .build();

                JSONObject jsonObject = new JSONObject();

                // For each of the traits get the Parent category it belongs for coloring purpose
                for (String trait : doc.getTraits()) {
                    String parent = childParentMap.get(trait);

                    if (parent == null) {
                        if (diseaseBranch.get(trait) != null) {
                            jsonObject.put(trait, "other disease");
                            Set<String> holder = parentChildHolder.get("other disease");
                            holder.add(trait);
                            parentChildHolder.put("other disease", holder);
                            parentChildCounter.put("other disease", parentChildCounter.get("other disease") + 1);
                        }
                        else if (measurementBranch.get(trait) != null) {
                            jsonObject.put(trait, "other measurement");
                            Set<String> holder = parentChildHolder.get("other measurement");
                            holder.add(trait);
                            parentChildHolder.put("other measurement", holder);
                            parentChildCounter.put("other measurement", parentChildCounter.get("other measurement") + 1);
                        }
                        else {
                            jsonObject.put(trait, "other trait");
                            Set<String> holder = parentChildHolder.get("other trait");
                            holder.add(trait);
                            parentChildHolder.put("other trait", holder);
                            parentChildCounter.put("other trait", parentChildCounter.get("other trait") + 1);
                        }
                    } else {
                        jsonObject.put(trait, parent);
                        parentChildCounter.put(parent, parentChildCounter.get(parent) + 1);
                        Set<String> holder = parentChildHolder.get(parent);
                        holder.add(trait);
                        parentChildHolder.put(parent, holder);
                    }
                }

                doc.setCategory(String.valueOf(jsonObject));
                doc.setResourceName("diagram");

                if (doc.getCytogeneticRegion() != null) {
                    solrService.save(doc);

                    HashMap<String, JSONObject> filteredCategoryMap = new HashMap<>();
                    Iterator<String> keys = jsonObject.keys();
                    while (keys.hasNext()) {
                        String trait = keys.next();
                        String parent = jsonObject.optString(trait);

                        filteredCategoryMap.putIfAbsent(parent, new JSONObject());
                        filteredCategoryMap.get(parent).put(trait, parent);
                    }

                    filteredCategoryMap.forEach((parent, filteredCategories) -> {
                        List<String> filteredTraits = new ArrayList<>();
                        Iterator<String> subKeys = filteredCategories.keys();
                        while (subKeys.hasNext()) {
                            String filteredTrait = subKeys.next();
                            filteredTraits.add(filteredTrait);
                        }

                        doc.setCategory(String.valueOf(filteredCategories));
                        doc.setFacet("by_parent_trait");
                        doc.setParentTrait(parent);
                        doc.setTraits(filteredTraits);
                        doc.setId(Operation.generateUUId());

                        if (!doc.getTraits().isEmpty()) {
                            solrService.save(doc);
                            log.info("Finished Facet data for parent-trait {} / chromosome - {} and region - {}", parent, doc.getChromosome(), doc.getCytogeneticRegion());
                        }
                    });

                    log.info("Traits - regions {} done", oneMapOfRegion.get("REGION_NAME"));
                }
            }
        }

        Operation.prettyPrint(parentChildCounter);
        Operation.prettyPrint(parentChildHolder);

        Map<String, Object> data = new HashMap<>();
        data.put("Categorized", parentChildHolder);
        data.put("SideBarCount", parentChildCounter);

        Doc doc2 = Doc.builder()
                .type("statistics")
                .data(mapper.writeValueAsString(data.get("SideBarCount")))
                .id(Operation.generateUUId())
                .resourceName("diagram")
                .build();
        solrService.save(doc2);

        return data;
    }


}
