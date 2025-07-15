package uk.ac.ebi.spot.gwas.service;

import uk.ac.ebi.spot.gwas.model.Gene;

import java.util.List;

public interface GeneRetrieveService {

    List<Gene> findGenesByIds(List<Long> mappedGeneIds);
}
