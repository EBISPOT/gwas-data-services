package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.deposition.domain.Submission;
import uk.ac.ebi.spot.gwas.model.Curator;
import uk.ac.ebi.spot.gwas.model.Publication;
import uk.ac.ebi.spot.gwas.model.Study;

public interface StudiesService {


    uk.ac.ebi.spot.gwas.deposition.domain.Study getMongoStudy(String studyId);



    Study processStudy(uk.ac.ebi.spot.gwas.deposition.domain.Study mongoStudy,
                      Curator curator,
                      Publication publication,
                      Submission submission);



    void publishSummaryStats(uk.ac.ebi.spot.gwas.deposition.domain.Study mongoStudy, Publication publication);

}
