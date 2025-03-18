package uk.ac.ebi.spot.gwas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.EfoTrait;

import java.util.List;
import java.util.Optional;

public interface EFOTraitRepository extends JpaRepository<EfoTrait, Long> {


}
