package uk.ac.ebi.spot.gwas.service.mapping;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.config.AppConfig;
import uk.ac.ebi.spot.gwas.constant.OperationMode;
import uk.ac.ebi.spot.gwas.dto.EnsemblData;
import uk.ac.ebi.spot.gwas.dto.MappingDto;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.service.loader.FileCacheService;
import uk.ac.ebi.spot.gwas.service.loader.SnpLoadingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EnsemblRunnner {

    private static final Integer DB_BATCH_SIZE = 1000;
    private Integer loadingThreadSize = 40;
    private static final Integer MAPPING_THREAD_SIZE = 1;

    @Autowired
    private AppConfig config;
    @Autowired
    private EnsemblService ensemblService;
    @Autowired
    private SnpLoadingService snpLoadingService;
    @Autowired
    private FileCacheService fileCacheService;
    @Autowired
    private DataSavingService dataSavingService;

    public void runCache(int customThreadSize) throws ExecutionException, InterruptedException, IOException {
        loadingThreadSize = customThreadSize;
        log.info("Caching Ensembl with {} thread size", loadingThreadSize);
        MappingDto mappingDto = snpLoadingService.getSnpsLinkedToLocus(OperationMode.MAP_SOME_SNPS_INDB, loadingThreadSize, DB_BATCH_SIZE);
        fileCacheService.cacheEnsemblData(mappingDto);
    }

    public void mapAssociations(OperationMode mode) throws ExecutionException, InterruptedException, IOException {
        log.info("Full remap commenced");
        long start = System.currentTimeMillis();
        List<MappingDto> mappingDtoList = new ArrayList<>();

        MappingDto mappingDto = snpLoadingService.getSnpsLinkedToLocus(mode, loadingThreadSize, DB_BATCH_SIZE);
        EnsemblData ensemblData = fileCacheService.cacheEnsemblData(mappingDto);
        List<Association> associations = snpLoadingService.getAssociationObjects(mode,
                                                                                 loadingThreadSize,
                                                                                 DB_BATCH_SIZE,
                                                                                 mappingDto.getTotalPagesToMap());
        int count = MAPPING_THREAD_SIZE;
        for (List<Association> associationList : ListUtils.partition(associations, MAPPING_THREAD_SIZE)) {
            try {
                List<CompletableFuture<MappingDto>> futureList =
                        associationList.stream()
                                .map(association -> ensemblService.mapAndSaveData(association, ensemblData, OperationMode.MAP_ALL_SNPS_INDB))
                                .collect(Collectors.toList());
                CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]));
                for (CompletableFuture<MappingDto> future : futureList) {
                    mappingDtoList.add(future.get());
                }
            } catch (Exception e) {
                log.error("Association was not mapped due to error {}", e.getMessage());
            }
            count += MAPPING_THREAD_SIZE;
            log.info("Finished Processing {} Association", count);
        }

        dataSavingService.postMappingReportCheck(associations);
        // dataSavingService.saveRestHistory(ensemblData, config.getERelease(), THREAD_SIZE)
        log.info("Total Association mapping time {}", (System.currentTimeMillis() - start));
        log.trace(String.valueOf(mappingDtoList));
    }
}
