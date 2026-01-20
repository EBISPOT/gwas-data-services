package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.ac.ebi.spot.gwas.deposition.domain.Submission;
import uk.ac.ebi.spot.gwas.model.Curator;
import uk.ac.ebi.spot.gwas.model.Publication;
import uk.ac.ebi.spot.gwas.model.Study;
import uk.ac.ebi.spot.gwas.model.StudyExtension;

import java.util.List;

public interface StudiesService {


    uk.ac.ebi.spot.gwas.deposition.domain.Study getMongoStudy(String studyId);



    void deleteChildrenByStudyId(Long studyId);

    void saveStudy(Study study);


    Study processStudy(uk.ac.ebi.spot.gwas.deposition.domain.Study mongoStudy,
                      Curator curator,
                      Publication publication,
                      Submission submission);

    void deleteStudiesForPublication(List<String> accessionIds);
}
