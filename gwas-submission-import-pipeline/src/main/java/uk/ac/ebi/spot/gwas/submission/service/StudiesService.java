package uk.ac.ebi.spot.gwas.submission.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.ac.ebi.spot.gwas.deposition.domain.Study;

import java.util.List;

public interface StudiesService {

   Page<Study> findBySubmissionId(String submissionId, Pageable pageable);

   Long findBySubmissionId(String submissionId);

   Boolean checkSumstatsExists(String submissionId);

}
