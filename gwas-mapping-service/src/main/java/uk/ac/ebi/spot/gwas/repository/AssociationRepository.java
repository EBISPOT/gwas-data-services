package uk.ac.ebi.spot.gwas.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.projection.MappingProjection;

import java.util.List;

@Repository
public interface AssociationRepository extends JpaRepository<Association, Long> {

    @Query("select association.id as associationId" +
            " FROM Association as association")
    List<MappingProjection> findAllWithFewAttributes(Pageable pageable);

    @Query("select association.id as associationId" +
            " FROM Association as association where association.lastMappingDate is null ")
    List<MappingProjection> findUnmappedWithFewAttributes(Pageable pageable);

    List<Association> findBylastMappingDateIsNull(Pageable pageable);

    @Query("Select association from Association as association " +
            "JOIN association.study study " +
            "WHERE study.id = :studyId ")
    List<Association> findAssociationByStudyId(@Param("studyId") Long studyId);
}
