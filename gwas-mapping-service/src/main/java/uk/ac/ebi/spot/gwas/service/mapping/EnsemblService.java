package uk.ac.ebi.spot.gwas.service.mapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.constant.OperationMode;
import uk.ac.ebi.spot.gwas.dto.*;
import uk.ac.ebi.spot.gwas.model.*;
import uk.ac.ebi.spot.gwas.service.data.MappingRecordService;
import uk.ac.ebi.spot.gwas.service.data.SecureUserRepository;
import uk.ac.ebi.spot.gwas.service.data.SingleNucleotidePolymorphismQueryService;
import uk.ac.ebi.spot.gwas.service.data.TrackingOperationService;
import uk.ac.ebi.spot.gwas.service.loader.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class EnsemblService {

    @Autowired
    private DataMappingService dataMappingService;
    @Autowired
    private DataSavingService dataSavingService;
    @Autowired
    private SingleNucleotidePolymorphismQueryService singleNucleotidePolymorphismQueryService;
    @Autowired
    private TrackingOperationService trackingOperationService;
    @Autowired
    private SecureUserRepository secureUserRepository;
    @Autowired
    private MappingRecordService mappingRecordService;

    @Async("asyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<MappingDto> mapAndSaveData(Association association, EnsemblData ensemblData, OperationMode mode) {

        log.info("commenced mapping and saving Association {} Data", association.getId());
        MappingDto mappingDto = MappingDto.builder().build();
        Collection<Locus> studyAssociationLoci = association.getLoci();

        for (Locus associationLocus : studyAssociationLoci) {
            Long locusId = associationLocus.getId();
            Collection<SingleNucleotidePolymorphism> snpsLinkedToLocus = singleNucleotidePolymorphismQueryService.findByRiskAllelesLociId(locusId);
            Collection<Gene> authorReportedGenesLinkedToSnp = associationLocus.getAuthorReportedGenes();

            Collection<String> authorReportedGeneNamesLinkedToSnp = new ArrayList<>();
            authorReportedGenesLinkedToSnp.forEach(g -> {
                if (g.getGeneName() != null) {
                    authorReportedGeneNamesLinkedToSnp.add(g.getGeneName().trim());
                }
            });
            for (SingleNucleotidePolymorphism snpLinkedToLocus : snpsLinkedToLocus) {
                String snpRsId = snpLinkedToLocus.getRsId();
                EnsemblMappingResult mappingResult = dataMappingService.mappingPipeline(ensemblData, snpRsId, authorReportedGeneNamesLinkedToSnp, mode);
                mappingDto = dataSavingService.saveMappedData(snpLinkedToLocus, mappingResult);
            }
        }

        dataSavingService.createAssociationReports(association, mappingDto);

        SecureUser user = secureUserRepository.findByEmail("automatic_mapping_process");
        String performer = "automatic_mapping_process";

        trackingOperationService.update(association, user, "ASSOCIATION_MAPPING");
        log.debug("Update mapping record");
        mappingRecordService.updateAssociationMappingRecord(association, new Date(), performer);

        log.info(" Mapping was successful ");
        return CompletableFuture.completedFuture(mappingDto);
    }
}
