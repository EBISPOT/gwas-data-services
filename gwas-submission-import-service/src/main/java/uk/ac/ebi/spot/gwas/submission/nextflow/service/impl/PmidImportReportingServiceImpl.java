package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.PmidImportReporting;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.PmidImportReportingRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.PmidImportReportingService;

@Slf4j
@Service
public class PmidImportReportingServiceImpl implements PmidImportReportingService {


    PmidImportReportingRepository pmidImportReportingRepository;


    public PmidImportReportingServiceImpl(PmidImportReportingRepository pmidImportReportingRepository) {
        this.pmidImportReportingRepository = pmidImportReportingRepository;
    }

    public PmidImportReporting findBySubmissionId(String submissionId) {
        return pmidImportReportingRepository.findBySubmissionId(submissionId).orElse(null);
    }


    public void savePmidReporting(String submissionId, Integer studiesImported) {
            log.info("studiesImported is {}",studiesImported);
            PmidImportReporting pmidImportReporting = findBySubmissionId(submissionId);
            int studiesCount = pmidImportReporting.getStudiesImported() != null ? pmidImportReporting.getStudiesImported() : 0 ;
            studiesCount += studiesImported;
            log.info("studiesCount is {}",studiesCount);
            pmidImportReporting.setStudiesImported(studiesCount);
            pmidImportReportingRepository.save(pmidImportReporting);
    }

}
