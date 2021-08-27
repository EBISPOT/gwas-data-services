package uk.ac.ebi.spot.gwas.service.mapping;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.constant.OperationMode;
import uk.ac.ebi.spot.gwas.dto.EnsemblData;
import uk.ac.ebi.spot.gwas.dto.MappingDto;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.service.data.AssociationService;
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

    private final EnsemblService ensemblService;
    private final SnpLoadingService snpLoadingService;
    private final FileCacheService fileCacheService;
    private final DataSavingService dataSavingService;
    @Autowired
    private AssociationService associationService;
    private EnsemblData ensemblData = EnsemblData.builder().build();

    public EnsemblRunnner(EnsemblService ensemblService,
                          SnpLoadingService snpLoadingService,
                          FileCacheService fileCacheService,
                          DataSavingService dataSavingService) {
        this.ensemblService = ensemblService;
        this.snpLoadingService = snpLoadingService;
        this.fileCacheService = fileCacheService;
        this.dataSavingService = dataSavingService;
    }

    public void runCache(int customThreadSize) throws ExecutionException, InterruptedException, IOException {
        loadingThreadSize = customThreadSize;
        log.info("Caching Ensembl with {} thread size", loadingThreadSize);
        MappingDto mappingDto = snpLoadingService.getSnpsLinkedToLocus(OperationMode.MAP_ALL_SNPS_INDB, loadingThreadSize, DB_BATCH_SIZE);
        fileCacheService.cacheEnsemblData(mappingDto);
    }

    public void mapAllAssociations(String performer) throws ExecutionException, InterruptedException, IOException {
        log.info("Full database remap commenced by performer: {}", performer);
        OperationMode mode = OperationMode.MAP_SOME_SNPS_INDB;
        MappingDto mappingDto = snpLoadingService.getSnpsLinkedToLocus(mode, loadingThreadSize, DB_BATCH_SIZE);
        ensemblData = fileCacheService.cacheEnsemblData(mappingDto);
        List<Association> associations = snpLoadingService.getAssociationObjects(mode,
                                                                                 loadingThreadSize,
                                                                                 DB_BATCH_SIZE,
                                                                                 mappingDto.getTotalPagesToMap());
        this.mapAssociations(OperationMode.MAP_ALL_SNPS_INDB, associations,ensemblData);
    }

    public void mapAssociationsByStudy(Long studyId, String performer) {
        log.info("Mapping -m {}", performer);
        OperationMode mode = OperationMode.MAP_SOME_SNPS_INDB;
        List<Association> associations = associationService.getAssociationsByStudy(studyId);
        this.mapAssociations(mode, associations, ensemblData);
    }

    public void mapSomeAssociations(String performer) throws ExecutionException, InterruptedException {
        log.info("Mapping -m {}", performer);
        OperationMode mode = OperationMode.MAP_SOME_SNPS_INDB;
        Page<Association> pageInfo = associationService.getAssociationPageInfo(0, DB_BATCH_SIZE);
        List<Association> associations = snpLoadingService.getAssociationObjects(mode,
                                                                                 loadingThreadSize,
                                                                                 DB_BATCH_SIZE,
                                                                                 pageInfo.getTotalPages());
        this.mapAssociations(mode, associations, ensemblData);
    }

    public void mapAssociations(OperationMode mode,
                                List<Association> associations,
                                EnsemblData ensemblData) {
        log.info("Full remap commenced for {} ", associations.size());
        long start = System.currentTimeMillis();
        List<MappingDto> mappingDtoList = new ArrayList<>();
        int count = MAPPING_THREAD_SIZE;

        for (List<Association> associationList : ListUtils.partition(associations, MAPPING_THREAD_SIZE)) {
            try {
                List<CompletableFuture<MappingDto>> futureList =
                        associationList.stream()
                                .map(association -> ensemblService.mapAndSaveData(association, ensemblData, mode))
                                .collect(Collectors.toList());
                CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]));
                for (CompletableFuture<MappingDto> future : futureList) {
                    mappingDtoList.add(future.get());
                }
            } catch (Exception e) {
                log.error("Association was not mapped due to error {}", e.getMessage());
            }
            log.info("Finished Processing {} Association", count);
            count += MAPPING_THREAD_SIZE;
        }

        dataSavingService.postMappingReportCheck(associations);
        // dataSavingService.saveRestHistory(ensemblData, config.getERelease(), THREAD_SIZE)
        log.info("Total Association mapping time {}", (System.currentTimeMillis() - start));
        log.trace(String.valueOf(mappingDtoList));
    }
}
