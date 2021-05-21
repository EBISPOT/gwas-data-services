package uk.ac.ebi.spot.gwas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnsemblData {

    private Map<String, Variation> variations;
    private Map<String, GeneSymbol> reportedGenes;
    private Map<String, List<OverlapRegion>> cytoGeneticBand;
    private Map<String, AssemblyInfo> assemblyInfo;
    private Map<String, List<OverlapGene>> ensemblOverlapGene;
    private Map<String, List<OverlapGene>> ncbiOverlapGene;
}
