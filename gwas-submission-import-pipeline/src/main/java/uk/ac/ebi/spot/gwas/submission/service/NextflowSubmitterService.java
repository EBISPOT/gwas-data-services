package uk.ac.ebi.spot.gwas.submission.service;

import uk.ac.ebi.spot.gwas.exception.SlurmProcessException;

public interface NextflowSubmitterService {


    void executePipeline(String pmid, String submissionId) throws SlurmProcessException;
}
