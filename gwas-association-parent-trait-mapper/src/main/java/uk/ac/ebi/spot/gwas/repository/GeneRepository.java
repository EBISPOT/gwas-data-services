package uk.ac.ebi.spot.gwas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.Gene;

import java.util.List;

public interface GeneRepository extends JpaRepository<Gene, Long> {

    @Override
    List<Gene> findAllById(Iterable<Long> iterable);
}
