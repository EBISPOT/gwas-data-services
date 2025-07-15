package uk.ac.ebi.spot.gwas.service;

import uk.ac.ebi.spot.gwas.model.Gene;

import java.util.List;

public interface AssociationUpdateService {

    void updateAssociationMappedGene(Long associationId, List<Gene> mappedGenes);
}
