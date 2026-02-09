package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.deposition.domain.Association;
import uk.ac.ebi.spot.gwas.model.Study;

import java.util.List;

public interface AssociationService {

   void saveAssociations(List<Association> associations, Study study);

   List<Association> getAssociations(String submissionId, String studyTag);
}
