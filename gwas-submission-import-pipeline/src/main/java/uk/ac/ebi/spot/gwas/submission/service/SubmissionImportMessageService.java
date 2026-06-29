package uk.ac.ebi.spot.gwas.submission.service;

public interface SubmissionImportMessageService {

    void sendMessage(String submissionId, String eventType, String event, String result, String email);
}
