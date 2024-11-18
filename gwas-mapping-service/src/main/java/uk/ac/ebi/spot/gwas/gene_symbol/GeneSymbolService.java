package uk.ac.ebi.spot.gwas.gene_symbol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.common.config.AppConfig;
import uk.ac.ebi.spot.gwas.common.constant.DataType;
import uk.ac.ebi.spot.gwas.common.constant.Type;
import uk.ac.ebi.spot.gwas.common.constant.Uri;
import uk.ac.ebi.spot.gwas.common.service.RestResponseResultBuilderService;
import uk.ac.ebi.spot.gwas.mapping.dto.RestResponseResult;
import uk.ac.ebi.spot.gwas.common.service.EnsemblRestcallHistoryService;
import uk.ac.ebi.spot.gwas.common.service.ApiService;
import uk.ac.ebi.spot.gwas.common.util.CacheUtil;
import uk.ac.ebi.spot.gwas.variation.Variant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GeneSymbolService {

    private final ObjectMapper mapper = new ObjectMapper();

    private final AppConfig config;
    private final EnsemblRestcallHistoryService historyService;
    private final ApiService mappingApiService;
    RestResponseResultBuilderService restResponseResultBuilderService;

    public GeneSymbolService(AppConfig config,
                             EnsemblRestcallHistoryService historyService,
                             ApiService mappingApiService,
                             RestResponseResultBuilderService restResponseResultBuilderService) {
        this.config = config;
        this.historyService = historyService;
        this.mappingApiService = mappingApiService;
        this.restResponseResultBuilderService = restResponseResultBuilderService;
    }

    public Map<String, GeneSymbol> getReportedGenes(int threadSize,
                                                    int batchSize,
                                                    List<String> snpRsIds) throws ExecutionException, InterruptedException {

        long start = System.currentTimeMillis();
        Map<String, GeneSymbol> cached = CacheUtil.reportedGenes(DataType.REPORTED_GENES, config.getCacheDir());
        int partitionSize = threadSize * batchSize;

        List<String> getFromApi = new ArrayList<>();
        for (String snpRsId : snpRsIds) {
            GeneSymbol geneSymbol = cached.get(snpRsId.trim());
            if (geneSymbol == null) {
                getFromApi.add(snpRsId);
            }
        }

        for (List<String> dataPartition : ListUtils.partition(getFromApi, partitionSize)) {
            List<CompletableFuture<Map<String, GeneSymbol>>> futureList = ListUtils.partition(dataPartition, batchSize)
                    .stream()
                    .map(listPart -> mappingApiService.geneSymbolPost(listPart)).collect(Collectors.toList());
            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]));
            for (CompletableFuture<Map<String, GeneSymbol>> future : futureList) {
                cached.putAll(future.get());
            }
        }
        log.info("Total reported gene api call time {}", (System.currentTimeMillis() - start));
        CacheUtil.saveToFile(DataType.REPORTED_GENES, config.getCacheDir(), cached);
        return cached;
    }

    @Cacheable(value = "geneSymbol")
    public GeneSymbol getReportedGeneFromDB(String gene) {
        log.warn("Retrieving Reported Gene for gene: {}", gene);
        RestResponseResult result = historyService.getHistoryByTypeParamAndVersion(Type.LOOKUP_SYMBOL, gene, config.getERelease());
        GeneSymbol geneSymbol = new GeneSymbol();
        if (result == null) {
            geneSymbol = this.restApiCall(gene);
        } else {
            try {
                log.info("inside getting geneSymbol from History block");
                if(result.getRestResult() != null ) {
                    geneSymbol = mapper.readValue(result.getRestResult(), GeneSymbol.class);
                } else{
                    geneSymbol = mapper.readValue(result.getError(), GeneSymbol.class);
                }

            } catch (JsonProcessingException e) { log.error(e.getMessage()); }
        }
        return geneSymbol;
    }

    public GeneSymbol restApiCall(String gene) { // chromosomeEnd
        String uri = String.format("%s/%s/%s", config.getServer(), Uri.REPORTED_GENES, gene);
        log.debug("GeneSymbol url is {}", uri);
        Optional<ResponseEntity<GeneSymbol>> optionalEntity = mappingApiService.getRequestGeneSymbol(uri);
        GeneSymbol geneSymbol = optionalEntity
                .map(response -> mapper.convertValue(response.getBody(), GeneSymbol.class))
                .orElseGet(GeneSymbol::new);
        String geneSymbolResponse = "";
        try {
            geneSymbolResponse =mapper.writeValueAsString(geneSymbol);
        } catch(Exception ex) {
            log.error("Exception in writing object as string in GeneSymbolService"+ex.getMessage(),ex);
        }
        restResponseResultBuilderService.buildResponseResult(uri, gene, Type.LOOKUP_SYMBOL, optionalEntity.get(),geneSymbolResponse);
        return geneSymbol;
    }

}
