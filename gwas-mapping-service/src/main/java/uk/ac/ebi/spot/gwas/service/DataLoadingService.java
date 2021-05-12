package uk.ac.ebi.spot.gwas.service;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.constant.DataType;
import uk.ac.ebi.spot.gwas.dto.MappingDto;
import uk.ac.ebi.spot.gwas.dto.Variation;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.util.CacheUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class DataLoadingService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AssociationService service;

    @Autowired
    private MappingApiService mappingApiService;

    @Value("${mapping.cache}/${mapping.version}/")
    private String cacheDir;

    public Map<String, Variation> getVariation(int threadSize,
                                               int batchSize,
                                               List<String> snpRsIds) throws ExecutionException, InterruptedException {

        long start = System.currentTimeMillis();
        Map<String, Variation> cached = CacheUtil.variation(DataType.VARIATION, cacheDir);
        Map<String, Variation> report = new HashMap<>();
        int partitionSize = threadSize * batchSize;

        List<String> getFromApi = new ArrayList<>();
        for (String snpRsId : snpRsIds) {
            Variation variation = cached.get(snpRsId.trim());
            if (variation != null) {
                report.putAll(Collections.singletonMap(snpRsId, variation));
                log.info("found {}", snpRsId);
            } else {
                log.info("not found {}", snpRsId);
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
                report.putAll(future.get());
            }
            CacheUtil.saveToFile(DataType.VARIATION, cacheDir, report);
        }
        log.info("Total variation api call time {}", (System.currentTimeMillis() - start));
        return report;
    }

    public MappingDto getSnpsLinkedToLocus(int threadSize, int batchSize) throws ExecutionException, InterruptedException {
        int pageStart = 0;
        int pageEnd = threadSize;
        long start = System.currentTimeMillis();
        Page<Association> associations = service.getAssociationPageInfo(pageStart, batchSize);
        log.info("Total elements is: {} Total pages is: {} ", associations.getTotalElements(), associations.getTotalPages());

        List<String> snpRsIds = new ArrayList<>();
        List<String> reportedGenes = new ArrayList<>();

        while (pageStart < associations.getTotalPages()) {
            List<Integer> dataPages = IntStream.range(pageStart, pageEnd).boxed().collect(Collectors.toList());

            List<CompletableFuture<MappingDto>> futureList =
                    dataPages.stream()
                            .map(dataPage -> service.getAssociationsBatch(dataPage, batchSize)).collect(Collectors.toList());

            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]));
            for (CompletableFuture<MappingDto> future : futureList) {
                snpRsIds.addAll(future.get().getSnpRsIds());
                reportedGenes.addAll(future.get().getReportedGenes());
            }
            pageStart += threadSize;
            pageEnd += threadSize;
        }
        log.info("Total time {}", (System.currentTimeMillis() - start));

        snpRsIds = snpRsIds.stream()
                .map(String::toLowerCase).distinct().collect(Collectors.toList());

        reportedGenes = reportedGenes.stream()
                .map(String::toLowerCase).distinct().collect(Collectors.toList());

        return MappingDto.builder()
                .snpRsIds(snpRsIds)
                .threadSize(threadSize)
                .batchSize(batchSize)
                .totalPagesToMap(associations.getTotalPages())
                .reportedGenes(reportedGenes).build();
    }

    public List<Association> getAssociationObjects(int threadSize, int batchSize, int totalPages) throws ExecutionException, InterruptedException {
        int pageStart = 0;
        int pageEnd = threadSize;
        long start = System.currentTimeMillis();
        log.info("Total elements is: {} ", totalPages);

        List<Association> associations = new ArrayList<>();
        while (pageStart < totalPages) {
            List<Integer> dataPages = IntStream.range(pageStart, pageEnd).boxed().collect(Collectors.toList());

            List<CompletableFuture<List<Association>>> futureList =
                    dataPages.stream()
                            .map(dataPage -> service.getAssociations(dataPage, batchSize)).collect(Collectors.toList());

            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]));
            for (CompletableFuture<List<Association>> future : futureList) {
                associations.addAll(future.get());
            }
            pageStart += threadSize;
            pageEnd += threadSize;
        }

        log.info("Total time {}", (System.currentTimeMillis() - start));
        return associations;
    }

}
