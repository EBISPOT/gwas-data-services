package uk.ac.ebi.spot.gwas.submission.service;

import uk.ac.ebi.spot.gwas.model.UnpublishedStudy;

import java.util.List;

public interface UnpublishedStudiesService {


    void cleanUpUnpublishedStudies(List<String> accessionIds);
}
