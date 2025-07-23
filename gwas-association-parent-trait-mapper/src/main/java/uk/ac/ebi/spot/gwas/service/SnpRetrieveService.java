package uk.ac.ebi.spot.gwas.service;

import uk.ac.ebi.spot.gwas.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.gwas.rest.projection.SnpGeneProjection;

import java.util.List;

public interface SnpRetrieveService {

    List<SnpGeneProjection> findOverLappingGenes(Long snpId, String source);

    List<SnpGeneProjection> findUpDownStreamGenes(Long snpId , String source);

    SingleNucleotidePolymorphism getSnp(Long snpId);
}
