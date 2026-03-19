package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.deposition.domain.Sample;
import uk.ac.ebi.spot.gwas.model.Study;

import java.util.List;

public interface SampleService {

    List<Sample> getSamples(String submissionId, String studyTag);

    void saveSamples(List<Sample> samples, Study study);
}
