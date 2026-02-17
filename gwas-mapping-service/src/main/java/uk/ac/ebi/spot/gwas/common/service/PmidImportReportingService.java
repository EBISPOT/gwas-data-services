package uk.ac.ebi.spot.gwas.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.common.model.PmidImportReporting;
import uk.ac.ebi.spot.gwas.common.repository.PmidImportReportingRepository;

@Slf4j
@Service
public class PmidImportReportingService {

    PmidImportReportingRepository pmidImportReportingRepository;


    public PmidImportReportingService(PmidImportReportingRepository pmidImportReportingRepository) {
        this.pmidImportReportingRepository = pmidImportReportingRepository;
    }

    public PmidImportReporting findBySubmissionId(String submissionId) {
        return pmidImportReportingRepository.findBySubmissionId(submissionId).orElse(null);
    }


    public void savePmidReporting(String submissionId, Integer asscnsMapped, String executionMode) {
        log.info("asscnsMapped is {}",asscnsMapped);
        PmidImportReporting pmidImportReporting = findBySubmissionId(submissionId);
        if(executionMode.equals("auto-import")) {
            int asscnCount = pmidImportReporting.getAssociationMapped() != null ? pmidImportReporting.getAssociationMapped() : 0;
            asscnCount += asscnsMapped;
            log.info("asscnCount is {}", asscnCount);
            pmidImportReporting.setAssociationMapped(asscnCount);
        }
        if(executionMode.equals("approve-snps")) {
            int asscnCount = pmidImportReporting.getAssociationApproved() != null ? pmidImportReporting.getAssociationApproved() : 0;
            asscnCount += asscnsMapped;
            log.info("asscnCount is {}", asscnCount);
            pmidImportReporting.setAssociationApproved(asscnCount);
        }
        if(executionMode.equals("publish-studies")) {
            int studiesCount = pmidImportReporting.getStudiesPublished() != null ? pmidImportReporting.getStudiesPublished() : 0;
            studiesCount += asscnsMapped;
            log.info("studiesCount is {}", studiesCount);
            pmidImportReporting.setStudiesPublished(studiesCount);
        }

        pmidImportReportingRepository.save(pmidImportReporting);
    }
}
