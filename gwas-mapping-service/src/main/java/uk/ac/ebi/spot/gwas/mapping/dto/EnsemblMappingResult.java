package uk.ac.ebi.spot.gwas.mapping.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.ac.ebi.spot.gwas.common.model.GenomicContext;
import uk.ac.ebi.spot.gwas.common.model.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class EnsemblMappingResult {

    private String rsId;

    private Integer merged = 0;

    private String currentSnpId;

    private Collection<Location> locations = new ArrayList<>();

    private Collection<GenomicContext> genomicContexts = new ArrayList<>();

    private ArrayList<String> pipelineErrors = new ArrayList<>();

    private String functionalClass;

    private Set<String> ncbiOverlappingGene = new HashSet<>();

    private Set<String> ensemblOverlappingGene = new HashSet<>();

    public void addPipelineErrors(String error) {
        ArrayList<String> pipelineErrors = getPipelineErrors();
        pipelineErrors.add(error);
        setPipelineErrors(pipelineErrors);
    }

    public void addGenomicContext(GenomicContext genomicContext) {
        Collection<GenomicContext> genomicContexts = getGenomicContexts();
        genomicContexts.add(genomicContext);
        setGenomicContexts(genomicContexts);
    }

    public void addNcbiOverlappingGene(String gene) {
        Set<String> ncbiOverlappingGene = getNcbiOverlappingGene();
        ncbiOverlappingGene.add(gene);
        setNcbiOverlappingGene(ncbiOverlappingGene);
    }

    public void addEnsemblOverlappingGene(String gene) {
        Set<String> ensemblOverlappingGene = getEnsemblOverlappingGene();
        ensemblOverlappingGene.add(gene);
        setEnsemblOverlappingGene(ensemblOverlappingGene);
    }

}
