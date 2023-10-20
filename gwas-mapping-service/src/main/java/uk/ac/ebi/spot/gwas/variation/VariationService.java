package uk.ac.ebi.spot.gwas.variation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.common.config.AppConfig;
import uk.ac.ebi.spot.gwas.common.constant.DataType;
import uk.ac.ebi.spot.gwas.common.constant.Type;
import uk.ac.ebi.spot.gwas.common.constant.Uri;
import uk.ac.ebi.spot.gwas.mapping.dto.Mapping;
import uk.ac.ebi.spot.gwas.mapping.dto.RestResponseResult;
import uk.ac.ebi.spot.gwas.common.service.EnsemblRestcallHistoryService;
import uk.ac.ebi.spot.gwas.common.service.ApiService;
import uk.ac.ebi.spot.gwas.common.util.CacheUtil;
import uk.ac.ebi.spot.gwas.common.util.MappingUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VariationService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final AppConfig config;
    private final EnsemblRestcallHistoryService historyService;
    private final ApiService mappingApiService;

    public VariationService(AppConfig config,
                            EnsemblRestcallHistoryService historyService,
                            ApiService mappingApiService) {
        this.config = config;
        this.historyService = historyService;
        this.mappingApiService = mappingApiService;
    }

    public Map<String, Variant> getVariation(int threadSize,
                                             int batchSize,
                                             List<String> snpRsIds) throws ExecutionException, InterruptedException {

        long start = System.currentTimeMillis();
        Map<String, Variant> cached = CacheUtil.variation(DataType.VARIATION, config.getCacheDir());
        int partitionSize = threadSize * batchSize;

        List<String> getFromApi = new ArrayList<>();
        for (String snpRsId : snpRsIds) {
            Variant variant = cached.get(snpRsId.trim());
            if (variant == null) {
                log.info("{} not found in file cache", snpRsId);
                getFromApi.add(snpRsId);
            }
        }
        log.info("Found {} in cache out of {} Remains: {}", snpRsIds.size() - getFromApi.size(), snpRsIds.size(), getFromApi.size());

        for (List<String> dataPartition : ListUtils.partition(getFromApi, partitionSize)) {
            List<CompletableFuture<Map<String, Variant>>> futureList = ListUtils.partition(dataPartition, batchSize)
                    .stream()
                    .map(listPart -> mappingApiService.variationPost(listPart)).collect(Collectors.toList());

            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]));
            for (CompletableFuture<Map<String, Variant>> future : futureList) {
                cached.putAll(future.get());
            }
            CacheUtil.saveToFile(DataType.VARIATION, config.getCacheDir(), cached);
        }
        log.info("Total variation api call time {}", (System.currentTimeMillis() - start));
        return cached;
    }

    public Map<String, Variant> getVariationsWhoseRsidHasChanged(Map<String, Variant> variantMap, List<String> snpRsIds) throws InterruptedException {
        int count = 1;
        for (String snpRsId : snpRsIds) {
            Variant variant = variantMap.get(snpRsId.trim());
            if (variant == null) {
                log.info("{} not found in batch or changed, now getting from Ensembl ...", snpRsId);
                variantMap.putAll(this.restApiCall(snpRsId));
            } else {
                log.info("{} already retrieved in batch", snpRsId);
            }
            MappingUtil.statusLog(DataType.VARIATION.name(), count++, snpRsIds.size());
        }
        CacheUtil.saveToFile(DataType.VARIATION, config.getCacheDir(), variantMap);
        return variantMap;
    }

    @Cacheable(value = "variation")
    public Variant getVariationFromDB(String snpRsId) {
        log.warn("Retrieving variation for snp: {}", snpRsId);
        RestResponseResult result = historyService.getHistoryByTypeParamAndVersion(Type.SNP, snpRsId, config.getERelease());
        Variant variant = new Variant();
        if (result == null) {
            variant = this.restApiCall(snpRsId).get(snpRsId);
        } else {
            try {
                variant = mapper.readValue(result.getRestResult(), Variant.class);
            } catch (JsonProcessingException e) {  log.error(e.getMessage()); }
        }
        return variant;
    }

    public Map<String, Variant> restApiCall(String snpRsId) {
        Map<String, Variant> variationMap = new HashMap<>();
        String uri = String.format("%s/%s/%s", config.getServer(), Uri.VARIATION, snpRsId);
        Variant variant = mappingApiService.getRequest(uri)
                .map(response -> mapper.convertValue(response.getBody(), Variant.class))
                .orElseGet(Variant::new);

        if(variant.getMappings() != null ) {
           List<Mapping> filteredMapping = variant.getMappings().stream()
                    .filter(mapping -> mapping.getCoordSystem().equals("chromosome"))
                    .collect(Collectors.toList());
            variant.setMappings(filteredMapping);
        }
        variationMap.put(snpRsId, variant);

        return variationMap;
    }

}
