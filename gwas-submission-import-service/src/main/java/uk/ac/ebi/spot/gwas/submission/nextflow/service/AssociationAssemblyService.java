package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.model.AssociationExtension;

public interface AssociationAssemblyService {

    Association assemble(uk.ac.ebi.spot.gwas.deposition.domain.Association mongoAssociation);

    AssociationExtension assembleAssociationExtension(uk.ac.ebi.spot.gwas.deposition.domain.Association mongoAssociation);
}
