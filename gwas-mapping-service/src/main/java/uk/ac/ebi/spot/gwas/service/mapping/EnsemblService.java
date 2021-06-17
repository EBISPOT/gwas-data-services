package uk.ac.ebi.spot.gwas.service.mapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.config.AppConfig;
import uk.ac.ebi.spot.gwas.constant.DataType;
import uk.ac.ebi.spot.gwas.dto.*;
import uk.ac.ebi.spot.gwas.model.*;
import uk.ac.ebi.spot.gwas.service.data.MappingRecordService;
import uk.ac.ebi.spot.gwas.service.data.SecureUserRepository;
import uk.ac.ebi.spot.gwas.service.data.SingleNucleotidePolymorphismQueryService;
import uk.ac.ebi.spot.gwas.service.data.TrackingOperationService;
import uk.ac.ebi.spot.gwas.util.MappingUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class EnsemblService {


    @Autowired
    private DataLoadingService dataLoadingService;
    @Autowired
    private AppConfig config;
    @Autowired
    private DataMappingService dataMappingService;
    @Autowired
    private  DataSavingService dataSavingService;
    @Autowired
    private SingleNucleotidePolymorphismQueryService singleNucleotidePolymorphismQueryService;

    @Autowired
    private TrackingOperationService trackingOperationService;
    @Autowired
    private SecureUserRepository secureUserRepository;
    @Autowired
    private MappingRecordService mappingRecordService;
    @Autowired
    private DataLoadingService dataService;

    private static final Integer API_BATCH_SIZE = 200;
    private static final Integer DB_BATCH_SIZE = 1000;

    public Object fullEnsemblRemapping() throws ExecutionException, InterruptedException, IOException {
        log.info("Full remap commenced");
        int threadSize = 15;

        EnsemblData ensemblData = cacheEnsemblData(threadSize);
        Association association = dataService.getOneAssocTest();
        return this.mapAndSaveData(association, ensemblData);
    }

    public EnsemblData cacheEnsemblData(int threadSize) throws ExecutionException, InterruptedException, IOException{
        MappingDto mappingDto = dataService.getSnpsLinkedToLocus(threadSize, DB_BATCH_SIZE);
        List<String> snpRsIds = mappingDto.getSnpRsIds();
        List<String> reportedGenes = mappingDto.getReportedGenes();
        return this.loadMappingData(snpRsIds, reportedGenes, threadSize, API_BATCH_SIZE);
    }











    public EnsemblData loadMappingData(List<String> snpRsIds,
                                       List<String> reportedGenes,
                                       int threadSize,
                                       int apiBatchSize) throws ExecutionException, InterruptedException, IOException {

        Path path = Paths.get(config.getCacheDir());
        Files.createDirectories(path);

        Map<String, GeneSymbol> reportedGeneMap = dataLoadingService.getReportedGenes(threadSize, apiBatchSize, reportedGenes);
        Map<String, Variation> variantMap = dataLoadingService.getVariation(threadSize, apiBatchSize, snpRsIds);
        variantMap = dataLoadingService.getVariationsWhoseRsidHasChanged(variantMap, snpRsIds);

        List<Variation> variants = new ArrayList<>();
        variantMap.forEach((k, v) -> {
            if (v.getMappings() != null) {
                variants.add(v);
            }
        });

        // Get CytoGenetic Bands
        List<String> locations = MappingUtil.getAllChromosomesAndPositions(variants);
        Map<String, List<OverlapRegion>> cytoGeneticBand = dataLoadingService.getCytoGeneticBands(DataType.CYTOGENETIC_BAND, locations);

        // Get Chromosome End
        List<String> chromosomes = MappingUtil.getAllChromosomes(variants);
        Map<String, AssemblyInfo> assemblyInfos = dataLoadingService.getAssemblyInfo(DataType.ASSEMBLY_INFO, chromosomes);

        // Get Overlapping genes
        Map<String, List<OverlapGene>> ensemblOverlappingGenes = dataLoadingService.getOverlappingGenes(DataType.ENSEMBL_OVERLAP_GENES, config.getEnsemblSource(), locations);
        Map<String, List<OverlapGene>> ncbiOverlappingGenes = dataLoadingService.getOverlappingGenes(DataType.NCBI_OVERLAP_GENES, config.getNcbiSource(), locations);

        // Get Upstream Genes
        List<String> upstreamLocations = MappingUtil.getUpstreamLocations(variants, config.getGenomicDistance());
        ensemblOverlappingGenes.putAll(dataLoadingService.getOverlappingGenes(DataType.ENSEMBL_UPSTREAM_GENES, config.getEnsemblSource(), upstreamLocations));
        ncbiOverlappingGenes.putAll(dataLoadingService.getOverlappingGenes(DataType.NCBI_UPSTREAM_GENES, config.getNcbiSource(), upstreamLocations));

        // Get Downstream Genes
        List<String> downStreamLocations = MappingUtil.getDownstreamLocations(variants, assemblyInfos, config.getGenomicDistance());
        ensemblOverlappingGenes.putAll(dataLoadingService.getOverlappingGenes(DataType.ENSEMBL_DOWNSTREAM_GENES, config.getEnsemblSource(), downStreamLocations));
        ncbiOverlappingGenes.putAll(dataLoadingService.getOverlappingGenes(DataType.NCBI_DOWNSTREAM_GENES, config.getNcbiSource(), downStreamLocations));

        return EnsemblData.builder()
                .variations(variantMap)
                .reportedGenes(reportedGeneMap)
                .cytoGeneticBand(cytoGeneticBand)
                .assemblyInfo(assemblyInfos)
                .ensemblOverlapGene(ensemblOverlappingGenes)
                .ncbiOverlapGene(ncbiOverlappingGenes)
                .build();
    }

    public MappingDto mapAndSaveData(Association association, EnsemblData ensemblData) {

        log.info("commenced saving Association {} Data", association.getId());
        MappingDto mappingDto = MappingDto.builder().build();
        Collection<Locus> studyAssociationLoci = association.getLoci();
        for (Locus associationLocus : studyAssociationLoci) {
            Long locusId = associationLocus.getId();
            log.info("Locus ID for this asscoiation {} is {}", association.getId(), locusId);
            Collection<SingleNucleotidePolymorphism> snpsLinkedToLocus = singleNucleotidePolymorphismQueryService.findByRiskAllelesLociId(locusId);
            Collection<Gene> authorReportedGenesLinkedToSnp = associationLocus.getAuthorReportedGenes();

            Collection<String> authorReportedGeneNamesLinkedToSnp = new ArrayList<>();
            authorReportedGenesLinkedToSnp.forEach(g -> {
                if (g.getGeneName() != null) {
                    authorReportedGeneNamesLinkedToSnp.add(g.getGeneName().trim());
                }
            });
            log.info("Reported Genes for this asscoiation {} ", authorReportedGeneNamesLinkedToSnp);
            for (SingleNucleotidePolymorphism snpLinkedToLocus : snpsLinkedToLocus) {
                String snpRsId = snpLinkedToLocus.getRsId();
                EnsemblMappingResult mappingResult = dataMappingService.mappingPipeline(ensemblData, snpRsId, authorReportedGeneNamesLinkedToSnp);
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
        return mappingDto;
    }
}