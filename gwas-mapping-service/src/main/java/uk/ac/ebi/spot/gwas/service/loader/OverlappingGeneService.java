package uk.ac.ebi.spot.gwas.service.loader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.config.AppConfig;
import uk.ac.ebi.spot.gwas.constant.DataType;
import uk.ac.ebi.spot.gwas.constant.Type;
import uk.ac.ebi.spot.gwas.constant.Uri;
import uk.ac.ebi.spot.gwas.dto.OverlapGene;
import uk.ac.ebi.spot.gwas.dto.OverlapRegion;
import uk.ac.ebi.spot.gwas.dto.RestResponseResult;
import uk.ac.ebi.spot.gwas.service.data.EnsemblRestcallHistoryService;
import uk.ac.ebi.spot.gwas.service.mapping.ApiService;
import uk.ac.ebi.spot.gwas.util.CacheUtil;
import uk.ac.ebi.spot.gwas.util.MappingUtil;

import java.util.*;

@Slf4j
@Service
public class OverlappingGeneService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final AppConfig config;
    private final EnsemblRestcallHistoryService historyService;
    private final ApiService mappingApiService;

    public OverlappingGeneService(AppConfig config,
                                  EnsemblRestcallHistoryService historyService,
                                  ApiService mappingApiService) {
        this.config = config;
        this.historyService = historyService;
        this.mappingApiService = mappingApiService;
    }

    public Map<String, List<OverlapGene>> getOverlappingGenes(DataType dataType,
                                                              String source,
                                                              List<String> locations) throws InterruptedException {
        int count = 1;
        Map<String, List<OverlapGene>> cached = CacheUtil.overlappingGenes(dataType, config.getCacheDir());
        for (String location : locations) {
            List<OverlapGene> genes = cached.get(location);
            if (genes == null) {
                cached.putAll(this.restApiCall(location, source));
            }
            MappingUtil.statusLog(dataType.name(), count++, locations.size());
        }
        CacheUtil.saveToFile(dataType, config.getCacheDir(), cached);
        return cached;
    }

    @Cacheable(value = "overlapGene")
    public List<OverlapGene> getOverlappingGeneFromDB(String location, String source) {
        log.info("Retrieving Overlapping Gene for {} @ location: {}", source, location);
        String param = String.format("%s?feature=gene", location);
        if (source.equals(config.getNcbiSource())) {
            param = String.format("%s&logic_name=%s&db_type=%s", param, config.getNcbiLogicName(), config.getNcbiDbType());
        }
        RestResponseResult result = historyService.getHistoryByTypeParamAndVersion(Type.OVERLAP_REGION, param, config.getERelease());
        List<OverlapGene> overlapGenes = new ArrayList<>();
        if (result == null) {
            overlapGenes = this.restApiCall(location, source).get(location);
        } else {
            try {
                overlapGenes = Arrays.asList(mapper.readValue(result.getRestResult(), OverlapGene[].class));
            } catch (JsonProcessingException e) { log.error(e.getMessage()); }
        }
        return overlapGenes;
    }

    public Map<String, List<OverlapGene>> restApiCall(String mappingLocation, String source) { // Ensembl Overlapping Genes
        String uri = String.format("%s/%s/%s?feature=gene", config.getServer(), Uri.OVERLAPPING_GENE_REGION, mappingLocation);
        if (source.equals(config.getNcbiSource())) {
            uri = String.format("%s&logic_name=%s&db_type=%s", uri, config.getNcbiLogicName(), config.getNcbiDbType());
        }
        Map<String, List<OverlapGene>> geneOverlap = new HashMap<>();
        List<OverlapGene> geneOverlapList = mappingApiService.getRequest(uri)
                .map(response -> mapper.convertValue(response.getBody(), new TypeReference<List<OverlapGene>>() {}))
                .orElseGet(ArrayList::new);
        geneOverlap.put(mappingLocation, geneOverlapList);
        return geneOverlap;
    }
}
