package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.ac.ebi.spot.gwas.deposition.domain.Submission;
import uk.ac.ebi.spot.gwas.model.Curator;
import uk.ac.ebi.spot.gwas.model.Publication;
import uk.ac.ebi.spot.gwas.model.Study;
import uk.ac.ebi.spot.gwas.model.StudyExtension;

public interface StudiesService {

    Page<Study> getStudies(String pmid, Pageable pageable);

    Long countStudies(String pmid);

    uk.ac.ebi.spot.gwas.deposition.domain.Study getMongoStudy(String studyId);

    Page<uk.ac.ebi.spot.gwas.deposition.domain.Study> getMongoStudies(String submissionId, Pageable pageable);

    Study findByAccessionId(String accessionId);

    void deleteChildrenByStudyId(Long studyId);

    void saveStudy(Study study);

    void saveStudyExtension(StudyExtension studyExtension);

    Study processStudy(uk.ac.ebi.spot.gwas.deposition.domain.Study mongoStudy,
                      Curator curator,
                      Publication publication,
                      Submission submission);
}
