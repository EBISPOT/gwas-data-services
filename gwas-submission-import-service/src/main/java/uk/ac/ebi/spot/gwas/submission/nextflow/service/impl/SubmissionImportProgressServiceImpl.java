package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.deposition.domain.Study;
import uk.ac.ebi.spot.gwas.deposition.domain.Submission;
import uk.ac.ebi.spot.gwas.model.Curator;
import uk.ac.ebi.spot.gwas.model.Publication;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class SubmissionImportProgressServiceImpl implements SubmissionImportProgressService {

    SubmissionService submissionService;

    CuratorService curatorService;

    PublicationService publicationService;

    StudiesService studiesService;

    public SubmissionImportProgressServiceImpl(SubmissionService submissionService,
                                               CuratorService curatorService,
                                               PublicationService publicationService,
                                               StudiesService studiesService) {
        this.submissionService = submissionService;
        this.curatorService = curatorService;
        this.publicationService = publicationService;
        this.studiesService = studiesService;
    }

    @Transactional
    public void importSubmission(String submissionId,
                                 List<String> studyIds,
                                 String curatorEmail,
                                 String pmid) {
        Curator curator = curatorService.findByEmail(curatorEmail);
        Publication publication = publicationService.findByPmid(pmid);
        Submission submission = submissionService.findBySubmissionId(submissionId);

        for (String studyId : studyIds) {
            Study momngoStudy = studiesService.getMongoStudy(studyId);
            if (momngoStudy != null) {
                String accessionId = momngoStudy.getAccession();
                log.info("Accession Id is {}", accessionId);
           /* uk.ac.ebi.spot.gwas.model.Study dbStudy = studiesService.findByAccessionId(accessionId);
            if(dbStudy != null) {
                studiesService.deleteChildrenByStudyId(dbStudy.getId());
            }*/
                uk.ac.ebi.spot.gwas.model.Study study = studiesService.processStudy(momngoStudy, curator, publication, submission);
                if(study != null) {
                    log.info("Publication study is {} {}", study.getAccessionId(), publication.getPubmedId());
                    Collection<uk.ac.ebi.spot.gwas.model.Study> pubStudies = publication.getStudies();
                    pubStudies.add(study);
                    publication.setStudies(pubStudies);
                    studiesService.saveStudy(study);
                }
                publicationService.save(publication);
            }
        }
    }


    @Transactional
    public void deleteStudiesForPublication(List<String> studyIds) {
        //Publication publication = publicationService.findByPmid(pmid);
        List<String> accessions = new ArrayList<>();
        for (String studyId : studyIds) {
            Study momngoStudy = studiesService.getMongoStudy(studyId);
            if (momngoStudy != null) {
                accessions.add(momngoStudy.getAccession());
            }
            studiesService.deleteStudiesForPublication(accessions);
        }
    }


}
