package uk.ac.ebi.spot.gwas.association;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.common.constant.OperationMode;
import uk.ac.ebi.spot.gwas.mapping.dto.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class SnpLoadingService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AssociationService service;

    public MappingDto getSnpsLinkedToLocus(OperationMode mode, int threadSize, int batchSize) throws ExecutionException, InterruptedException {
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
                            .map(dataPage -> service.getAssociationsBatch(dataPage, batchSize, mode)).collect(Collectors.toList());

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

    public List<Association> getAssociationInBatch(OperationMode mode,
                                                   int threadSize,
                                                   int batchSize,
                                                   int totalPages) throws ExecutionException, InterruptedException {
        int pageStart = 0;
        int pageEnd = threadSize;
        long start = System.currentTimeMillis();
        log.info("Total elements is: {} ", totalPages);

        List<Association> associations = new ArrayList<>();
        while (pageStart < totalPages) {
            List<Integer> dataPages = IntStream.range(pageStart, pageEnd).boxed().collect(Collectors.toList());

            List<CompletableFuture<List<Association>>> futureList =
                    dataPages.stream()
                            .map(dataPage -> service.getAssociations(dataPage, batchSize, mode)).collect(Collectors.toList());

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
