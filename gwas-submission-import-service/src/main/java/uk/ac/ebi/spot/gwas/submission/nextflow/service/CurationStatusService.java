package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.model.CurationStatus;

public interface  CurationStatusService {
    CurationStatus findByStatus(String curationStatus);
}
