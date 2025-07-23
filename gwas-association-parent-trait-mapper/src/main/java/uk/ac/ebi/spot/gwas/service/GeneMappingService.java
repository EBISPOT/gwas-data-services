package uk.ac.ebi.spot.gwas.service;

import uk.ac.ebi.spot.gwas.model.Gene;
import uk.ac.ebi.spot.gwas.rest.projection.AssociationGeneProjection;
import uk.ac.ebi.spot.gwas.rest.projection.SnpGeneProjection;

import java.util.List;

public interface GeneMappingService {

    void updateGeneMapping(List<AssociationGeneProjection> associationGeneProjections);

    void updateSnpGeneMapping(List<SnpGeneProjection> snpGeneProjections);

}
