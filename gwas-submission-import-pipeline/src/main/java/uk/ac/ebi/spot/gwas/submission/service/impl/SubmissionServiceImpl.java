package uk.ac.ebi.spot.gwas.submission.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.deposition.domain.Association;
import uk.ac.ebi.spot.gwas.deposition.domain.Publication;
import uk.ac.ebi.spot.gwas.deposition.domain.Study;
import uk.ac.ebi.spot.gwas.deposition.domain.Submission;
import uk.ac.ebi.spot.gwas.submission.constants.SubmissionType;
import uk.ac.ebi.spot.gwas.submission.mongo.repository.SubmissionRepository;
import uk.ac.ebi.spot.gwas.submission.service.AssociationService;
import uk.ac.ebi.spot.gwas.submission.service.PublicationService;
import uk.ac.ebi.spot.gwas.submission.service.StudiesService;
import uk.ac.ebi.spot.gwas.submission.service.SubmissionService;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    SubmissionRepository submissionRepository;

    PublicationService publicationService;

    StudiesService studiesService;

    AssociationService associationService;

    public SubmissionServiceImpl(SubmissionRepository submissionRepository,
                                 PublicationService publicationService,
                                 StudiesService studiesService,
                                 AssociationService associationService) {
        this.submissionRepository = submissionRepository;
        this.publicationService = publicationService;
        this.studiesService = studiesService;
    }

    public Submission findById(String submissionId) {
       return submissionRepository.findById(submissionId).orElse(null);
    }

    public SubmissionType getSubmissionType(Submission submission) {
        if(submission.getBodyOfWorks() != null &&  !submission.getBodyOfWorks().isEmpty() ) {
            return SubmissionType.PRE_PUBLISHED;
        } else if (submission.getPublicationId() != null  ) {
           Publication publication = publicationService.findByPublicationId(submission.getPublicationId());
            boolean hasSumStats = false;
            boolean hasMetadata = false;
            boolean hasAssociations = false;
           if(publication != null) {
               if(publication.getStatus().equals("UNDER_SUBMISSION")) {
                   hasMetadata = true;
               } else if(publication.getStatus().equals("UNDER_SUMMARY_STATS_SUBMISSION")) {
                   hasSumStats = true;
               }
              if(studiesService.checkSumstatsExists(submission.getId())) {
                  hasSumStats = true;
              }
              if(associationService.checkAssociationExists(submission.getId())) {
                  hasAssociations = true;
              }

               if (hasMetadata && hasSumStats && hasAssociations) {
                   return SubmissionType.METADATA_AND_SUM_STATS_AND_TOP_ASSOCIATIONS;
               }
               if (hasMetadata && hasSumStats && !hasAssociations) {
                   return SubmissionType.METADATA_AND_SUM_STATS;
               }
               if (hasMetadata && !hasSumStats && hasAssociations) {
                   return SubmissionType.METADATA_AND_TOP_ASSOCIATIONS;
               }
               if (hasMetadata && !hasSumStats && !hasAssociations) {
                   return SubmissionType.METADATA;
               }
               if (!hasMetadata && hasSumStats && !hasAssociations) {
                   return SubmissionType.SUM_STATS;
               }
           }
        }
        return SubmissionType.UNKNOWN;
    }


}
