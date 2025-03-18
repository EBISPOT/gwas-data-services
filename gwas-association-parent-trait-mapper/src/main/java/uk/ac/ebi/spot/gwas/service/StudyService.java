package uk.ac.ebi.spot.gwas.service;

import uk.ac.ebi.spot.gwas.model.EfoTrait;

public interface StudyService {

    void loadStudiesWithParentEfo(EfoTrait parenEfo);
}
