package uk.ac.ebi.spot.gwas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.model.Study;

public interface StudyRepository extends JpaRepository<Study,Long> {

    Page<Study> findByEfoTraitsShortForm(String shortForm, Pageable pageable);

    Long countStudiesByEfoTraitsShortForm(String shortForm);
}
