package uk.ac.ebi.spot.gwas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.ac.ebi.spot.gwas.model.Study;

import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {


    @Query("select s.id as studyId from Study s "+
    "join s.publicationId as p "+
    "join s.housekeeping as h "+
    "join h.curationStatus as cs "+
    "where p.pubmedId = :pmid "
    +" and cs.status != 'Requires Review'")
   List<Long> findStudiesByPmid(@Param("pmid") String pmid);
}
