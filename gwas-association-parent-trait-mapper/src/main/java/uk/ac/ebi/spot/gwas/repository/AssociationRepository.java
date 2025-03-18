package uk.ac.ebi.spot.gwas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.model.EfoTrait;

import java.util.List;
import java.util.Optional;

public interface AssociationRepository extends JpaRepository<Association, Long> {

   Page<Association> findByEfoTraitsShortForm(String shortForm, Pageable pageable);

   Long countAssociationsByEfoTraitsShortForm(String shortForm);
}
