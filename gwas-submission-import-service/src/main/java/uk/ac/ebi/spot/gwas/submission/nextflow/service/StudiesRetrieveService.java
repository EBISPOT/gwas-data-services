package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.ac.ebi.spot.gwas.model.Study;

import java.util.List;

public interface StudiesRetrieveService {

    Page<Study> getStudies(Long publicationId, Pageable pageable);

    Long countStudies(Long publicationId);

    List<Study> findByAccessionIds(List<String> accessionIds);
}
