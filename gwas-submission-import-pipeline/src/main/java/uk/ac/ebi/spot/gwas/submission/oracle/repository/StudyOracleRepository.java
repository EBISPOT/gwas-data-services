package uk.ac.ebi.spot.gwas.submission.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.ac.ebi.spot.gwas.model.Study;
import uk.ac.ebi.spot.gwas.rest.projection.StudyAccessionIdProjection;
import uk.ac.ebi.spot.gwas.rest.projection.StudyProjection;

import java.util.List;


public interface StudyOracleRepository extends JpaRepository<Study, Long> {

    @Query("select s.id as id, s.accessionId as accessionId from Study s "+
            "join s.publicationId as p "+
            "where p.pubmedId = :pmid")
    List<StudyAccessionIdProjection> findAccessionIdsByPmid(@Param("pmid") String pmid);



}
