package uk.ac.ebi.spot.gwas.updateefo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.updateefo.domain.EfoTrait;

import java.util.List;

public interface EfoTraitRepository extends MongoRepository<EfoTrait, Long> {

    Page<EfoTrait> findAll(Pageable pageable);
    List<EfoTrait> findByShortFormIn(List<String> shortForms);

}
