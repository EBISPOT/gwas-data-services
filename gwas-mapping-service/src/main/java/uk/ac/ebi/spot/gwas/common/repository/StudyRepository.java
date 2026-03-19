package uk.ac.ebi.spot.gwas.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.common.model.Study;

import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {

   List<Study> findByIdIsIn(List<Long> ids);
}
