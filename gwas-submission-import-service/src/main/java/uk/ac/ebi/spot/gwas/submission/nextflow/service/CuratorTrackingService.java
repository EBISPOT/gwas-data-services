package uk.ac.ebi.spot.gwas.submission.nextflow.service;

public interface CuratorTrackingService {

    void deleteCuratorTrackingHistory(Long studyId);
}
