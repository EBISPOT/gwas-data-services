package uk.ac.ebi.spot.gwas.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.spot.gwas.dto.EnsemblData;
import uk.ac.ebi.spot.gwas.dto.MappingDto;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.service.mapping.AssociationService;
import uk.ac.ebi.spot.gwas.service.mapping.DataLoadingService;
import uk.ac.ebi.spot.gwas.service.mapping.DataMappingService;
import uk.ac.ebi.spot.gwas.service.mapping.EnsemblService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
public class MappingController {

    @Autowired
    private AssociationService associationService;
    @Autowired
    private EnsemblService ensemblService;
    @Autowired
    private DataLoadingService dataService;
    @Autowired
    private DataMappingService dataMappingService;

    @GetMapping("/mapping-asynch")
    public Object mapDataAsynch() throws ExecutionException, InterruptedException, IOException {
        log.info("Full remap commenced");
        int threadSize = 15;
        int dbBatchSize = 1000;
        int apiBatchSize = 200;

        MappingDto mappingDto = dataService.getSnpsLinkedToLocus(threadSize, dbBatchSize);

        // List<Association> associations = dataService.getAssociationObjects(threadSize, dbBatchSize, mappingDto.getTotalPagesToMap());
        Association association = dataService.getOneAssocTest();

        List<String> snpRsIds = mappingDto.getSnpRsIds();
        List<String> reportedGenes = mappingDto.getReportedGenes();
        EnsemblData ensemblData = ensemblService.loadMappingData(snpRsIds, reportedGenes, threadSize, apiBatchSize);
        return ensemblService.mapAndSaveData(association, ensemblData);

    }

}
