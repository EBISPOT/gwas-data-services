package uk.ac.ebi.spot.gwas.service;

import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.model.EfoTrait;
import uk.ac.ebi.spot.gwas.model.Study;

public interface ParentEFOUpdateService {

    void saveAssociationWithParentEfo(Association association, EfoTrait parentEfo);

    void saveStudyWithParentEfo(Study study, EfoTrait parentEfo);


}
