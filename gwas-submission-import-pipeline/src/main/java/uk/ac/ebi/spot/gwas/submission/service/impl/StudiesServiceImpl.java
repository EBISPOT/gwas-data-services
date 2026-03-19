package uk.ac.ebi.spot.gwas.submission.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.deposition.domain.Study;
import uk.ac.ebi.spot.gwas.rest.projection.StudyAccessionIdProjection;
import uk.ac.ebi.spot.gwas.rest.projection.StudyProjection;
import uk.ac.ebi.spot.gwas.submission.mongo.repository.StudyRepository;
import uk.ac.ebi.spot.gwas.submission.oracle.repository.StudyOracleRepository;
import uk.ac.ebi.spot.gwas.submission.service.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StudiesServiceImpl implements StudiesService {

    StudyRepository studyRepository;

    StudyOracleRepository studyOracleRepository;

    AssociationService associationService;

    AncestryService ancestryService;

    CuratorTrackingService curatorTrackingService;

    NoteService noteService;

    public StudiesServiceImpl(StudyRepository studyRepository,
                              StudyOracleRepository studyOracleRepository,
                              AssociationService associationService,
                              AncestryService ancestryService,
                              CuratorTrackingService curatorTrackingService,
                              NoteService noteService) {
        this.studyRepository = studyRepository;
        this.studyOracleRepository = studyOracleRepository;
        this.associationService = associationService;
        this.ancestryService = ancestryService;
        this.curatorTrackingService = curatorTrackingService;
        this.noteService = noteService;
    }

    public Page<Study> findBySubmissionId(String submissionId, Pageable pageable) {
        return studyRepository.findBySubmissionId(submissionId, pageable);
    }

    public Long findBySubmissionId(String submissionId) {
        return studyRepository.countStudiesBySubmissionId(submissionId);
        //return studyRepository.findBySubmissionId(submissionId).count();
    }

    public Boolean checkSumstatsExists(String submissionId) {
        Long totalStudies = findBySubmissionId(submissionId);
        Long bucketStudies = totalStudies / 1000;
        for (int i = 0; i <= bucketStudies; i++) {
            Pageable pageable = PageRequest.of(i, 1000);
            Page<Study> studies = findBySubmissionId(submissionId, pageable);
            for (Study study : studies) {
                if (study.getSummaryStatisticsFile() != null && !study.getSummaryStatisticsFile().isEmpty()
                        && study.getSummaryStatisticsFile().equals("NR")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Transactional
    public void deleteStudies(List<Long> studyIds) {
        studyIds.forEach(studyId -> {
            deleteChildrenByStudyId(studyId);
            deleteStudy(studyId);
        });

    }

    private void deleteChildrenByStudyId(Long studyId) {
        //log.info("Inside delete children for studies");
        associationService.deleteAssociation(studyId);
        ancestryService.deleteAncestries(studyId);
        curatorTrackingService.deleteCuratorTrackingHistory(studyId);
        noteService.deleteNotes(studyId);
    }

    private void deleteStudy(Long studyId) {
        studyOracleRepository.deleteById(studyId);
    }


    @Transactional
    public List<StudyAccessionIdProjection> findAccessionIdsByPubmedId(String pmid) {
        return studyOracleRepository.findAccessionIdsByPmid(pmid);
    }

}