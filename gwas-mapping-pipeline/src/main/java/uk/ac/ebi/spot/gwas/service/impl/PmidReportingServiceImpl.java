package uk.ac.ebi.spot.gwas.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.PmidImportReporting;
import uk.ac.ebi.spot.gwas.repository.PmidImportReportingRepository;
import uk.ac.ebi.spot.gwas.service.PmidReportingService;
import uk.ac.ebi.spot.gwas.service.PublicationService;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class PmidReportingServiceImpl implements PmidReportingService {

    PmidImportReportingRepository pmidImportReportingRepository;

    PublicationService publicationService;

    public PmidReportingServiceImpl(PmidImportReportingRepository pmidImportReportingRepository,
                                    PublicationService publicationService) {
        this.pmidImportReportingRepository = pmidImportReportingRepository;
        this.publicationService = publicationService;
    }

    public  PmidImportReporting findBySubmissionId(String submissionId) {
        return pmidImportReportingRepository.findBySubmissionId(submissionId).orElse(null);
    }


    public List<PmidImportReporting> findByStatus(String status) {
        return pmidImportReportingRepository.findByStatus(status);
    }

    public void save(String submissionId, String status) {
        PmidImportReporting pmidImportReporting = findBySubmissionId(submissionId);
        log.info("Association Imported count is {}", pmidImportReporting.getAssociationMapped());
        log.info("Association approved count is {}", pmidImportReporting.getAssociationApproved());
        log.info("Studies published count is {}", pmidImportReporting.getStudiesPublished());
        pmidImportReporting.setStatus(status);
        if(status.equals("MAPPING_FAILED")) {
            pmidImportReporting.setAssociationMapped(0);
        }
        if(status.equals("SNP_APPROVAL_FAILED")) {
            pmidImportReporting.setAssociationApproved(0);
        }
        if(status.equals("PUBLISH_FAILED")) {
            pmidImportReporting.setStudiesPublished(0);
        }
        if(status.equals("PUBLISH_COMPLETED")) {
            pmidImportReporting.setCompletionDate(new Date());
            publicationService.updateCurationStatus(pmidImportReporting.getPublication().getPubmedId(), pmidImportReporting.getCuratorEmail());
        }
        pmidImportReportingRepository.save(pmidImportReporting);
    }

    public PmidImportReporting save(PmidImportReporting pmidImportReporting) {
        return pmidImportReportingRepository.save(pmidImportReporting);
    }

}
