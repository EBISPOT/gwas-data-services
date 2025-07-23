package uk.ac.ebi.spot.gwas.service;

import uk.ac.ebi.spot.gwas.model.Gene;

import java.util.List;

public interface SnpUpdateService {

    void updateSnpMappedGene(Long snpId, List<Gene> mappedGenes);
}
