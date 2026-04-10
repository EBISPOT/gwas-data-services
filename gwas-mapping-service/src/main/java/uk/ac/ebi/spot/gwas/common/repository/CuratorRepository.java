package uk.ac.ebi.spot.gwas.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.common.model.Curator;

public interface CuratorRepository extends JpaRepository<Curator, Long> {

    Curator findByLastName(String lastName);
}
