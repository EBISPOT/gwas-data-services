package uk.ac.ebi.spot.gwas.submission.service.impl;

import org.apache.commons.collections4.ListUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.deposition.domain.Curator;
import uk.ac.ebi.spot.gwas.deposition.domain.Publication;
import uk.ac.ebi.spot.gwas.deposition.domain.Study;
import uk.ac.ebi.spot.gwas.deposition.domain.Submission;
import uk.ac.ebi.spot.gwas.deposition.dto.curation.SubmissionRabbitMessage;
import uk.ac.ebi.spot.gwas.submission.mongo.repository.SubmissionRepository;
import uk.ac.ebi.spot.gwas.submission.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubmissionImportServiceImpl implements SubmissionImportService {

    SubmissionService submissionService;

    PublicationService publicationService;

    StudiesService studiesService;

    SubmissionImportProgressService submissionImportProgressService;

    CuratorService curatorService;

    NextflowJobMapperService nextflowJobMapperService;

    public SubmissionImportServiceImpl(SubmissionService submissionService,
                                       PublicationService publicationService,
                                       StudiesService studiesService,
                                       SubmissionImportProgressService submissionImportProgressService,
                                       CuratorService curatorService,
                                       NextflowJobMapperService nextflowJobMapperService) {
        this.submissionService = submissionService;
        this.publicationService = publicationService;
        this.studiesService = studiesService;
        this.submissionImportProgressService = submissionImportProgressService;
        this.curatorService = curatorService;
        this.nextflowJobMapperService = nextflowJobMapperService;
    }

    public void importSubmission(SubmissionRabbitMessage submissionRabbitMessage) {
       String submissionId = submissionRabbitMessage.getSubmissionId();
       Submission submission = submissionService.findById(submissionId);
       if(submission != null) {
           Publication publication = publicationService.findByPublicationId(submission.getPublicationId());

           if(publication != null) {
               Curator curator = curatorService.findById(publication.getCuratorId());
               if(!submissionImportProgressService.checkSubmissionExists(submissionId)) {
                   submissionImportProgressService.saveNewSubmission(submissionId, curator);
                   Long totalStudies = studiesService.findBySubmissionId(submissionId);
                   Long bucket = totalStudies/120;
                   List<String> mongoStudyIds = new ArrayList<>();
                   for(int i = 0 ; i <= bucket ; i++) {
                       Pageable pageable = PageRequest.of(i, 120);
                       Page<Study> studies = studiesService.findBySubmissionId(submissionId, pageable);
                       mongoStudyIds.addAll(studies.stream().map(Study::getId).collect(Collectors.toList()));
                   }
                   for (List<String> partIds : ListUtils.partition(mongoStudyIds, 30)) {
                       nextflowJobMapperService.writeJobMapFile(partIds);
                   }
               }
           }
       }

    }

}
