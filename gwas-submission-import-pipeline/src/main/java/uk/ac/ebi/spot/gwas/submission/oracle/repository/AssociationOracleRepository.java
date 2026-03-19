package uk.ac.ebi.spot.gwas.submission.oracle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.rest.projection.AssociationIdProjection;

import java.util.List;

public interface AssociationOracleRepository extends JpaRepository<Association, Long> {

    Long countByStudyId(Long studyId);

    Page<Association> findByStudyId(Long studyId, Pageable pageable);

    @Query( " select a.id as associationId from Association a join "+
    "a.study as s "+
    "where s.id = :studyId")
    List<AssociationIdProjection> findByStudyId(@Param("studyId") Long studyId);

    void  deleteAssociationByStudyId(Long studyId);
}
