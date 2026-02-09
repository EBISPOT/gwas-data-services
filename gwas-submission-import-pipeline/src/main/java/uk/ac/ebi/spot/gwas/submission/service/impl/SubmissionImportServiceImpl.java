package uk.ac.ebi.spot.gwas.submission.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.annotation.nextflow.dto.NextflowJobDTO;
import uk.ac.ebi.spot.gwas.deposition.domain.Curator;
import uk.ac.ebi.spot.gwas.deposition.domain.Publication;
import uk.ac.ebi.spot.gwas.deposition.domain.Study;
import uk.ac.ebi.spot.gwas.deposition.domain.Submission;
import uk.ac.ebi.spot.gwas.exception.SlurmProcessException;
import uk.ac.ebi.spot.gwas.model.PmidImportReporting;
import uk.ac.ebi.spot.gwas.submission.config.NextFlowJobConfig;
import uk.ac.ebi.spot.gwas.submission.service.*;
import uk.ac.ebi.spot.gwas.submission.util.FileHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SubmissionImportServiceImpl implements SubmissionImportService {

    SubmissionService submissionService;

    PublicationService publicationService;

    StudiesService studiesService;

    SubmissionImportProgressService submissionImportProgressService;

    CuratorService curatorService;

    NextflowJobMapperService nextflowJobMapperService;

    NextflowSubmitterService nextflowSubmitterService;

    NextFlowJobConfig nextFlowJobConfig;

    PmidImportReportingService pmidImportReportingService;


    public SubmissionImportServiceImpl(SubmissionService submissionService,
                                       PublicationService publicationService,
                                       StudiesService studiesService,
                                       SubmissionImportProgressService submissionImportProgressService,
                                       CuratorService curatorService,
                                       NextflowJobMapperService nextflowJobMapperService,
                                       NextflowSubmitterService nextflowSubmitterService,
                                       NextFlowJobConfig nextFlowJobConfig,
                                       PmidImportReportingService pmidImportReportingService
    ) {
        this.submissionService = submissionService;
        this.publicationService = publicationService;
        this.studiesService = studiesService;
        this.submissionImportProgressService = submissionImportProgressService;
        this.curatorService = curatorService;
        this.nextflowJobMapperService = nextflowJobMapperService;
        this.nextflowSubmitterService = nextflowSubmitterService;
        this.nextFlowJobConfig = nextFlowJobConfig;
        this.pmidImportReportingService = pmidImportReportingService;

    }

    public void importSubmission(String submissionId, String curatorEmail) throws SlurmProcessException {
        Submission submission = submissionService.findById(submissionId);
        if(submission != null) {
            Publication publication = publicationService.findByPublicationId(submission.getPublicationId());

            if(publication != null) {
                Curator curator = curatorService.findById(publication.getCuratorId());
                if(!submissionImportProgressService.checkSubmissionExists(submissionId)) {
                    log.info("Submission exists {}", submissionId);
                    submissionImportProgressService.saveNewSubmission(submissionId, curatorEmail);
                    Long totalStudies = studiesService.findBySubmissionId(submissionId);
                    log.info("Total studies in submissionId {} {}", submissionId,  totalStudies);
                    int partitionSize = Integer.parseInt(nextFlowJobConfig.getPartitionSize());
                    Long bucket = totalStudies/partitionSize;
                    List<String> mongoStudyIds = new ArrayList<>();
                    for(int i = 0 ; i <= bucket ; i++) {
                        Pageable pageable = PageRequest.of(i, partitionSize);
                        Page<Study> studies = studiesService.findBySubmissionId(submissionId, pageable);
                        mongoStudyIds.addAll(studies.stream().map(Study::getId).collect(Collectors.toList()));
                    }
                    List<NextflowJobDTO> nextflowJobDTOS = new ArrayList<>();
                    for (List<String> partIds : ListUtils.partition(mongoStudyIds, partitionSize)) {
                        String partMongoIds = String.join("_", partIds);
                        nextflowJobDTOS.add(NextflowJobDTO.builder()
                                .submissionId(submissionId)
                                .submissionType(submissionService.getSubmissionType(submission).name())
                                .curatorEmail(curatorEmail)
                                .studyIds(partMongoIds)
                                .pmid(publication.getPmid())
                                .build());
                    }
                    nextflowJobMapperService.writeJobMapFile(nextflowJobDTOS, publication.getPmid(), submissionId);
                    nextflowSubmitterService.executePipeline(publication.getPmid(),submissionId);
                }
            }
        }

    }


    @Transactional
    public void  deleteSubmissionInProgressEntry(String submissionId) {
        submissionImportProgressService.deleteSubmissionInProgressEntry(submissionId);
    }


    @Transactional
    public void savePmidReporting(String submissionId, String status) {
        pmidImportReportingService.save(submissionId, status );
    }

    @Transactional
    public void savePmidReporting(PmidImportReporting pmidImportReporting) {
        pmidImportReportingService.save(pmidImportReporting);
    }




}
