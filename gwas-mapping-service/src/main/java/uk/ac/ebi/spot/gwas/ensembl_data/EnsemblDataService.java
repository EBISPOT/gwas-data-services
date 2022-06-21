package uk.ac.ebi.spot.gwas.ensembl_data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.assembly_info.AssemblyInfo;
import uk.ac.ebi.spot.gwas.assembly_info.AssemblyInfoService;
import uk.ac.ebi.spot.gwas.common.config.AppConfig;
import uk.ac.ebi.spot.gwas.common.constant.DataType;
import uk.ac.ebi.spot.gwas.mapping.dto.*;
import uk.ac.ebi.spot.gwas.common.util.MappingUtil;
import uk.ac.ebi.spot.gwas.gene_symbol.GeneSymbol;
import uk.ac.ebi.spot.gwas.gene_symbol.GeneSymbolService;
import uk.ac.ebi.spot.gwas.overlap_gene.OverlapGene;
import uk.ac.ebi.spot.gwas.overlap_gene.OverlappingGeneService;
import uk.ac.ebi.spot.gwas.overlap_region.OverlapRegionService;
import uk.ac.ebi.spot.gwas.overlap_region.OverlapRegion;
import uk.ac.ebi.spot.gwas.variation.Variant;
import uk.ac.ebi.spot.gwas.variation.VariationService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class EnsemblDataService {

    @Autowired
    private OverlapRegionService overlapRegionService;
    @Autowired
    private VariationService variationService;
    @Autowired
    private GeneSymbolService geneSymbolService;
    @Autowired
    private AssemblyInfoService assemblyInfoService;
    @Autowired
    private OverlappingGeneService overlappingGeneService;

    @Autowired
    private AppConfig config;

    private static final Integer API_BATCH_SIZE = 200;
    private static final Integer DB_BATCH_SIZE = 1000;
    private static final Integer THREAD_SIZE = 15;

    public EnsemblData cacheEnsemblData(MappingDto mappingDto) throws ExecutionException, InterruptedException, IOException {
        List<String> snpRsIds = mappingDto.getSnpRsIds();
        List<String> reportedGenes = mappingDto.getReportedGenes();

        Path path = Paths.get(config.getCacheDir());
        Files.createDirectories(path);

        Map<String, GeneSymbol> reportedGeneMap = geneSymbolService.getReportedGenes(THREAD_SIZE, API_BATCH_SIZE, reportedGenes);

        Map<String, Variant> variantMap = variationService.getVariation(THREAD_SIZE, API_BATCH_SIZE, snpRsIds);
        variantMap = variationService.getVariationsWhoseRsidHasChanged(variantMap, snpRsIds);

        List<Variant> variants = new ArrayList<>();
        variantMap.forEach((k, v) -> {
            if (v.getMappings() != null) {
                variants.add(v);
            }
        });

        // Get CytoGenetic Bands
        List<String> locations = MappingUtil.getAllChromosomesAndPositions(variants);
        Map<String, List<OverlapRegion>> cytoGeneticBand = overlapRegionService.getCytoGeneticBands(DataType.CYTOGENETIC_BAND, locations);

        // Get Chromosome End
        List<String> chromosomes = MappingUtil.getAllChromosomes(variants);
        Map<String, AssemblyInfo> assemblyInfos = assemblyInfoService.getAssemblyInfo(DataType.ASSEMBLY_INFO, chromosomes);

        // Get Overlapping genes
        Map<String, List<OverlapGene>> ensemblOverlappingGenes = overlappingGeneService.getOverlappingGenes(DataType.ENSEMBL_OVERLAP_GENES, config.getEnsemblSource(), locations);
        Map<String, List<OverlapGene>> ncbiOverlappingGenes = overlappingGeneService.getOverlappingGenes(DataType.NCBI_OVERLAP_GENES, config.getNcbiSource(), locations);

        // Get Upstream Genes
        List<String> upstreamLocations = MappingUtil.getUpstreamLocations(variants, config.getGenomicDistance());
        ensemblOverlappingGenes.putAll(overlappingGeneService.getOverlappingGenes(DataType.ENSEMBL_UPSTREAM_GENES, config.getEnsemblSource(), upstreamLocations));
        ncbiOverlappingGenes.putAll(overlappingGeneService.getOverlappingGenes(DataType.NCBI_UPSTREAM_GENES, config.getNcbiSource(), upstreamLocations));

        // Get Downstream Genes
        List<String> downStreamLocations = MappingUtil.getDownstreamLocations(variants, assemblyInfos, config.getGenomicDistance());
        ensemblOverlappingGenes.putAll(overlappingGeneService.getOverlappingGenes(DataType.ENSEMBL_DOWNSTREAM_GENES, config.getEnsemblSource(), downStreamLocations));
        ncbiOverlappingGenes.putAll(overlappingGeneService.getOverlappingGenes(DataType.NCBI_DOWNSTREAM_GENES, config.getNcbiSource(), downStreamLocations));

        return EnsemblData.builder()
                .variations(variantMap)
                .reportedGenes(reportedGeneMap)
                .cytoGeneticBand(cytoGeneticBand)
                .assemblyInfo(assemblyInfos)
                .ensemblOverlapGene(ensemblOverlappingGenes)
                .ncbiOverlapGene(ncbiOverlappingGenes)
                .build();
    }

}
