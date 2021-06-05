package uk.ac.ebi.spot.gwas.service.mapping;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.constant.DataType;
import uk.ac.ebi.spot.gwas.dto.*;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.util.CacheUtil;
import uk.ac.ebi.spot.gwas.util.MappingUtil;

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

    public Map<String, Variation> getVariationsWhoseRsidHasChanged(Map<String, Variation> variantMap, List<String> snpRsIds) throws InterruptedException {
        int count = 1;
        for (String snpRsId : snpRsIds) {
            Variation variation = variantMap.get(snpRsId.trim());
            if (variation == null) {
                log.info("{} not found in batch or changed, now getting from Ensembl ...", snpRsId);
                variantMap.putAll(mappingApiService.variationGet(snpRsId));
            }else {
                log.info("{} already retrieved in batch", snpRsId);
            }
            MappingUtil.statusLog(DataType.VARIATION.name(), count++, snpRsIds.size());
        }
        CacheUtil.saveToFile(DataType.VARIATION, cacheDir, variantMap);
        return variantMap;
    }

    public Map<String, GeneSymbol> getReportedGenes(int threadSize,
                                                    int batchSize,
                                                    List<String> snpRsIds) throws ExecutionException, InterruptedException {

        long start = System.currentTimeMillis();
        Map<String, GeneSymbol> cached = CacheUtil.reportedGenes(DataType.REPORTED_GENES, cacheDir);
        Map<String, GeneSymbol> report = new HashMap<>();
        int partitionSize = threadSize * batchSize;

        List<String> getFromApi = new ArrayList<>();
        for (String snpRsId : snpRsIds) {
            GeneSymbol geneSymbol = cached.get(snpRsId.trim());
            if (geneSymbol != null) {
                report.putAll(Collections.singletonMap(snpRsId, geneSymbol));
            } else {
                getFromApi.add(snpRsId);
            }
        }

        for (List<String> dataPartition : ListUtils.partition(getFromApi, partitionSize)) {
            List<CompletableFuture<Map<String, GeneSymbol>>> futureList = ListUtils.partition(dataPartition, batchSize)
                    .stream()
                    .map(listPart -> mappingApiService.geneSymbolPost(listPart)).collect(Collectors.toList());

            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]));
            for (CompletableFuture<Map<String, GeneSymbol>> future : futureList) {
                report.putAll(future.get());
            }
        }
        log.info("Total reported gene api call time {}", (System.currentTimeMillis() - start));
        CacheUtil.saveToFile(DataType.REPORTED_GENES, cacheDir, report);
        return report;
    }

    public Map<String, List<OverlapRegion>> getCytoGeneticBands(DataType dataType, List<String> locations) throws InterruptedException {
        int count = 1;
        Map<String, List<OverlapRegion>> cached = CacheUtil.cytoGeneticBand(dataType, cacheDir);
        Map<String, List<OverlapRegion>> cytoGeneticBand = new HashMap<>();

        for (String location : locations) {
            List<OverlapRegion> regions = cached.get(location);
            if (regions == null) {
                cytoGeneticBand.putAll(mappingApiService.overlapBandRegion(location));
            } else {
                cytoGeneticBand.putAll(Collections.singletonMap(location, regions));
            }
            MappingUtil.statusLog(dataType.name(), count++, locations.size());
        }
        CacheUtil.saveToFile(dataType, cacheDir, cytoGeneticBand);
        return cytoGeneticBand;
    }


    public Map<String, AssemblyInfo> getAssemblyInfo(DataType dataType, List<String> chromosomes) throws InterruptedException { // Get Chromosome End
        int count = 1;
        Map<String, AssemblyInfo> cached = CacheUtil.assemblyInfo(dataType, cacheDir);
        Map<String, AssemblyInfo> assemblyInfos = new HashMap<>();

        for (String chromosome : chromosomes) {
            AssemblyInfo assemblyInfo = cached.get(chromosome);
            if (assemblyInfo == null) {
                assemblyInfos.putAll(mappingApiService.assemblyInfo(chromosome));
            } else {
                assemblyInfos.putAll(Collections.singletonMap(chromosome, assemblyInfo));
            }
            MappingUtil.statusLog(dataType.name(), count++, chromosomes.size());
        }
        CacheUtil.saveToFile(dataType, cacheDir, assemblyInfos);
        return assemblyInfos;
    }

    public Map<String, List<OverlapGene>> getOverlappingGenes(DataType dataType,
                                                              String source,
                                                              List<String> locations) throws InterruptedException {
        int count = 1;
        Map<String, List<OverlapGene>> cached = CacheUtil.overlappingGenes(dataType, cacheDir);
        Map<String, List<OverlapGene>> overlappingGenes = new HashMap<>();

        for (String location : locations) {
            List<OverlapGene> genes = cached.get(location);
            if (genes == null) {
                overlappingGenes.putAll(mappingApiService.overlapGeneRegion(location, source));
            } else {
                overlappingGenes.putAll(Collections.singletonMap(location, genes));
            }
            MappingUtil.statusLog(dataType.name(), count++, locations.size());
        }
        CacheUtil.saveToFile(dataType, cacheDir, overlappingGenes);
        return overlappingGenes;
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
