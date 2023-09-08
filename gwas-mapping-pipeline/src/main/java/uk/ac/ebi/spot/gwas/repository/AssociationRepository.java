package uk.ac.ebi.spot.gwas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.gwas.model.Association;

import java.util.List;

@Repository
public interface AssociationRepository extends JpaRepository<Association, Long> {


    @Override
    List<Association> findAllById(Iterable<Long> iterable);
}
