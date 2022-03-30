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

    public Map<String, Variation> getVariation(int threadSize,
                                               int batchSize,
                                               List<String> snpRsIds) throws ExecutionException, InterruptedException {

        long start = System.currentTimeMillis();
        Map<String, Variation> cached = CacheUtil.variation(DataType.VARIATION, config.getCacheDir());
        int partitionSize = threadSize * batchSize;

        List<String> getFromApi = new ArrayList<>();
        for (String snpRsId : snpRsIds) {
            Variation variation = cached.get(snpRsId.trim());
            if (variation == null) {
                log.info("{} not found in file cache", snpRsId);
                getFromApi.add(snpRsId);
            }
        }
        log.info("Found {} in cache out of {} Remains: {}", snpRsIds.size() - getFromApi.size(), snpRsIds.size(), getFromApi.size());

        for (List<String> dataPartition : ListUtils.partition(getFromApi, partitionSize)) {
            List<CompletableFuture<Map<String, Variation>>> futureList = ListUtils.partition(dataPartition, batchSize)
                    .stream()
                    .map(listPart -> mappingApiService.variationPost(listPart)).collect(Collectors.toList());

            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]));
            for (CompletableFuture<Map<String, Variation>> future : futureList) {
                cached.putAll(future.get());
            }
            CacheUtil.saveToFile(DataType.VARIATION, config.getCacheDir(), cached);
        }
        log.info("Total variation api call time {}", (System.currentTimeMillis() - start));
        return cached;
    }

    public Map<String, Variation> getVariationsWhoseRsidHasChanged(Map<String, Variation> variantMap, List<String> snpRsIds) throws InterruptedException {
        int count = 1;
        for (String snpRsId : snpRsIds) {
            Variation variation = variantMap.get(snpRsId.trim());
            if (variation == null) {
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
    public Variation getVariationFromDB(String snpRsId) {
        log.warn("Retrieving variation for snp: {}", snpRsId);
        RestResponseResult result = historyService.getHistoryByTypeParamAndVersion(Type.SNP, snpRsId, config.getERelease());
        Variation variation = new Variation();
        if (result == null) {
            variation = this.restApiCall(snpRsId).get(snpRsId);
        } else {
            try {
                variation = mapper.readValue(result.getRestResult(), Variation.class);
            } catch (JsonProcessingException e) {  log.error(e.getMessage()); }
        }
        return variation;
    }

    public Map<String, Variation> restApiCall(String snpRsId) {
        Map<String, Variation> variationMap = new HashMap<>();
        String uri = String.format("%s/%s/%s", config.getServer(), Uri.VARIATION, snpRsId);

        Variation variation = mappingApiService.getRequest(uri)
                .map(response -> mapper.convertValue(response.getBody(), Variation.class))
                .orElseGet(Variation::new);
        variationMap.put(snpRsId, variation);
        return variationMap;
    }

}
