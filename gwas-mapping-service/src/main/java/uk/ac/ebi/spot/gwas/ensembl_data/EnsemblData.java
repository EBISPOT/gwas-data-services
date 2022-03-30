package uk.ac.ebi.spot.gwas.ensembl_data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.ac.ebi.spot.gwas.assembly_info.AssemblyInfo;
import uk.ac.ebi.spot.gwas.gene_symbol.GeneSymbol;
import uk.ac.ebi.spot.gwas.overlap_gene.OverlapGene;
import uk.ac.ebi.spot.gwas.overlap_region.OverlapRegion;
import uk.ac.ebi.spot.gwas.variation.Variation;

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
