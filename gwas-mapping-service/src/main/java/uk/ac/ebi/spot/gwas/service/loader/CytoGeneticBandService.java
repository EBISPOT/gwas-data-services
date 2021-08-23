package uk.ac.ebi.spot.gwas.service.loader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.config.AppConfig;
import uk.ac.ebi.spot.gwas.constant.DataType;
import uk.ac.ebi.spot.gwas.constant.Type;
import uk.ac.ebi.spot.gwas.constant.Uri;
import uk.ac.ebi.spot.gwas.dto.OverlapRegion;
import uk.ac.ebi.spot.gwas.dto.RestResponseResult;
import uk.ac.ebi.spot.gwas.service.data.EnsemblRestcallHistoryService;
import uk.ac.ebi.spot.gwas.service.mapping.ApiService;
import uk.ac.ebi.spot.gwas.util.CacheUtil;
import uk.ac.ebi.spot.gwas.util.MappingUtil;

import java.util.*;

@Slf4j
@Service
public class CytoGeneticBandService {

    private static final ObjectMapper mapper = new ObjectMapper();

    private final AppConfig config;
    private final EnsemblRestcallHistoryService historyService;
    private final ApiService mappingApiService;

    public CytoGeneticBandService(AppConfig config,
                                  EnsemblRestcallHistoryService historyService,
                                  ApiService mappingApiService) {
        this.config = config;
        this.historyService = historyService;
        this.mappingApiService = mappingApiService;
    }

    public Map<String, List<OverlapRegion>> getCytoGeneticBands(DataType dataType, List<String> locations) throws InterruptedException {
        int count = 1;
        Map<String, List<OverlapRegion>> cached = CacheUtil.cytoGeneticBand(dataType, config.getCacheDir());
        for (String location : locations) {
            List<OverlapRegion> regions = cached.get(location);
            if (regions == null) {
                cached.putAll(this.restApiCall(location));
            }
            MappingUtil.statusLog(dataType.name(), count++, locations.size());
        }
        CacheUtil.saveToFile(dataType, config.getCacheDir(), cached);
        return cached;
    }

    @Cacheable(value = "cytogeneticBand")
    public List<OverlapRegion> getCytoGeneticBandsFromDB(String location) {
        log.info("Retrieving Cytogenetic Band for location: {}", location);
        String param = String.format("%s?feature=band", location);
        RestResponseResult result = historyService.getHistoryByTypeParamAndVersion(Type.OVERLAP_REGION, param, config.getERelease());
        List<OverlapRegion> overlapRegions = new ArrayList<>();
        if (result == null) {
            overlapRegions = this.restApiCall(location).get(location);
        } else {
            try {
                overlapRegions = Arrays.asList(mapper.readValue(result.getRestResult(), OverlapRegion[].class));
            } catch (JsonProcessingException e) { log.error(e.getMessage()); }
        }
        return overlapRegions;
    }

    public Map<String, List<OverlapRegion>> restApiCall(String mappingLocation) {
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        String uri = String.format("%s/%s/%s?feature=band", config.getServer(), Uri.OVERLAP_BAND_REGION, mappingLocation);
        List<OverlapRegion> overlapRegions = mappingApiService.getRequest(uri)
                .map(response -> mapper.convertValue(response.getBody(), new TypeReference<List<OverlapRegion>>() {}))
                .orElseGet(ArrayList::new);

        Map<String, List<OverlapRegion>> bands = new HashMap<>();
        bands.put(mappingLocation, overlapRegions);
        return bands;
    }
}

