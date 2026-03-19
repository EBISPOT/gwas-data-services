package uk.ac.ebi.spot.gwas.submission.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.PmidImportReporting;
import uk.ac.ebi.spot.gwas.submission.oracle.repository.PmidImportReportingRepository;
import uk.ac.ebi.spot.gwas.submission.service.PmidImportReportingService;

import java.util.List;

@Slf4j
@Service
public class PmidImportReportingServiceImpl implements PmidImportReportingService {

    PmidImportReportingRepository pmidImportReportingRepository;

    public PmidImportReportingServiceImpl(PmidImportReportingRepository pmidImportReportingRepository) {
        this.pmidImportReportingRepository = pmidImportReportingRepository;
    }

    public  PmidImportReporting findBySubmissionId(String submissionId) {
        return pmidImportReportingRepository.findBySubmissionId(submissionId).orElse(null);
    }


    public List<PmidImportReporting> findByStatus(String status) {
        return pmidImportReportingRepository.findByStatus(status);
    }


    public void save(String submissionId, String status) {
        PmidImportReporting pmidImportReporting = findBySubmissionId(submissionId);
        log.info("Studies Imported count is {}", pmidImportReporting.getStudiesImported());
        pmidImportReporting.setStatus(status);
        pmidImportReportingRepository.save(pmidImportReporting);
    }


    public PmidImportReporting save(PmidImportReporting pmidImportReporting) {
        return pmidImportReportingRepository.save(pmidImportReporting);
    }




}
