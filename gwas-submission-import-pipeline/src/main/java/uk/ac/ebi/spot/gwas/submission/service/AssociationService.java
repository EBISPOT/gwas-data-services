package uk.ac.ebi.spot.gwas.submission.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.ac.ebi.spot.gwas.deposition.domain.Association;

import java.util.List;

public interface AssociationService {

  Page<Association> findBySubmissionId(String submissionId, Pageable pageable);

  Long findBySubmissionId(String submissionId);

  Boolean checkAssociationExists(String submissionId);

  void deleteAssociation(Long studyId);
}
