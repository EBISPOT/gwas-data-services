package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.deposition.domain.Study;
import uk.ac.ebi.spot.gwas.deposition.domain.Submission;
import uk.ac.ebi.spot.gwas.model.Curator;
import uk.ac.ebi.spot.gwas.model.Publication;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.*;

import java.util.List;

@Slf4j
@Service
public class SubmissionImportProgressServiceImpl implements SubmissionImportProgressService {

    SubmissionService submissionService;

    CuratorService curatorService;

    PublicationService publicationService;

    StudiesService studiesService;

    PmidImportReportingService pmidImportReportingService;

    public SubmissionImportProgressServiceImpl(SubmissionService submissionService,
                                               CuratorService curatorService,
                                               PublicationService publicationService,
                                               StudiesService studiesService,
                                               PmidImportReportingService pmidImportReportingService) {
        this.submissionService = submissionService;
        this.curatorService = curatorService;
        this.publicationService = publicationService;
        this.studiesService = studiesService;
        this.pmidImportReportingService = pmidImportReportingService;
    }

    @Transactional
    public Integer importSubmission(String submissionId,
                                 List<String> studyIds,
                                 String curatorEmail,
                                 String pmid) {
        Curator curator = curatorService.findByEmail(curatorEmail);
        Publication publication = publicationService.findByPmid(pmid);
        Submission submission = submissionService.findBySubmissionId(submissionId);
        int studiesImported = 0;
        for (String studyId : studyIds) {
            Study momngoStudy = studiesService.getMongoStudy(studyId);
            if (momngoStudy != null) {
                String accessionId = momngoStudy.getAccession();
                log.info("Accession Id is {}", accessionId);
                uk.ac.ebi.spot.gwas.model.Study study = studiesService.processStudy(momngoStudy, curator, publication, submission);
                studiesImported++;
            }
        }
        return studiesImported;

    }

    @Transactional
    public Integer publishSummaryStats(String submissionId,
                             List<String> studyIds,
                                String pmid) {
        int studiesImported = 0;
        Publication publication = publicationService.findByPmid(pmid);
        for (String studyId : studyIds) {
            Study momngoStudy = studiesService.getMongoStudy(studyId);
            if (momngoStudy != null) {
                String accessionId = momngoStudy.getAccession();
                log.info("Accession Id is {}", accessionId);
                studiesService.publishSummaryStats(momngoStudy, publication);
                studiesImported++;
            }
        }
        return studiesImported;
    }

    @Transactional
    public void savePmidReporting(String submissionId, Integer studiesImported) {
        pmidImportReportingService.savePmidReporting(submissionId, studiesImported);
    }



}
