package uk.ac.ebi.spot.gwas.submission.service;

import uk.ac.ebi.spot.gwas.model.UnpublishedStudy;

public interface UnpublishedStudiesRetrieveService {

  UnpublishedStudy findByAccession(String accessionId);
}
