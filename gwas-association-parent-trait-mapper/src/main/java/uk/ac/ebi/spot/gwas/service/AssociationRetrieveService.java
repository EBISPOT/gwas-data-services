package uk.ac.ebi.spot.gwas.service;

import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.rest.projection.AssociationGeneProjection;

import java.util.List;

public interface AssociationRetrieveService {

    List<AssociationGeneProjection> findOverLappingGenes(Long accsnId, String source, Long snpId);

    List<AssociationGeneProjection> findUpDownStreamGenes(Long accsnId , String source, Long snpId);

    Association getAssociation(Long accsnId);
}
