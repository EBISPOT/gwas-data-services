package uk.ac.ebi.spot.gwas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.gwas.model.AssociationReport;

import java.util.Collection;

@Repository
public interface AssociationReportRepository extends JpaRepository<AssociationReport, Long> {

    AssociationReport findByAssociationId(Long id);


    @Query(value = "select * from association_report where association_id in (" +
            "select id from "+
            "(select a.*, rownum rnum from (select * from association order by id) a where rownum <= :maxRow) "+
            " where rnum >= :minRow )",
            nativeQuery = true)
    Collection<AssociationReport> findAllLSF(@Param("minRow") Integer minRow, @Param("maxRow") Integer maxRow);

    @Query(value = "DELETE FROM ASSOCIATION_REPORT WHERE ASSOCIATION_ID = :associationId ",
            nativeQuery = true)
    void deleteByAssociationId(@Param("associationId") Long associationId);
}

