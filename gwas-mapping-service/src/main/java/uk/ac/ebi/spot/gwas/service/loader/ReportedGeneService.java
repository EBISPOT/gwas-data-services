package uk.ac.ebi.spot.gwas.service.loader;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.config.AppConfig;
import uk.ac.ebi.spot.gwas.constant.DataType;
import uk.ac.ebi.spot.gwas.dto.GeneSymbol;
import uk.ac.ebi.spot.gwas.service.data.EnsemblRestcallHistoryService;
import uk.ac.ebi.spot.gwas.service.mapping.MappingApiService;
import uk.ac.ebi.spot.gwas.util.CacheUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportedGeneService {

    private final AppConfig config;
    private final EnsemblRestcallHistoryService historyService;
    private final MappingApiService mappingApiService;

    public ReportedGeneService(AppConfig config,
                            EnsemblRestcallHistoryService historyService,
                            MappingApiService mappingApiService) {
        this.config = config;
        this.historyService = historyService;
        this.mappingApiService = mappingApiService;
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
}
