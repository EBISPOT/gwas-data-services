package uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.Study;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface StudyRepository extends JpaRepository<Study , Long> {

   Page<Study> findByPublicationIdPubmedId(String pmid, Pageable pageable);

   Long countByPublicationIdPubmedId(String pmid);

   List<Study> findByAccessionIdIn(List<String> accessionIds);

   Page<Study> findByPublicationIdId(Long publicationId, Pageable pageable);

   Long countStudiesByPublicationIdId(Long publicationId);

}
