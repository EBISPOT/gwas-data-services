package uk.ac.ebi.spot.gwas.service;

public interface StudiesService {

    void publishStudiesForPmid(String pubmedId, String submissionId,  String outputDir, String errorDir, String mode);

}
