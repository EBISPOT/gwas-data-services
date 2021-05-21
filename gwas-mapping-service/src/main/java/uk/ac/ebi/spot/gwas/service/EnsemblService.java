package uk.ac.ebi.spot.gwas.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.config.AppConfig;
import uk.ac.ebi.spot.gwas.constant.DataType;
import uk.ac.ebi.spot.gwas.dto.*;
import uk.ac.ebi.spot.gwas.util.MappingUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class EnsemblService {


    @Autowired
    private DataLoadingService dataLoadingService;
    @Autowired
    private AppConfig config;

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

        return EnsemblData.builder()
                .variations(variantMap)
                .reportedGenes(reportedGeneMap)
                .cytoGeneticBand(cytoGeneticBand)
                .assemblyInfo(assemblyInfos)
                .build();
    }
}
