package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.model.Study;
import uk.ac.ebi.spot.gwas.model.StudyExtension;

public interface StudyAssemblyService {

    Study assemble(uk.ac.ebi.spot.gwas.deposition.domain.Study mongoStudy);

    StudyExtension assembleStudyExtension(uk.ac.ebi.spot.gwas.deposition.domain.Study mongoStudy);
}
