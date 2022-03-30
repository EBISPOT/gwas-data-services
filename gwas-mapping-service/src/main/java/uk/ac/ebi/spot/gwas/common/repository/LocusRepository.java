package uk.ac.ebi.spot.gwas.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.gwas.common.model.Locus;
import uk.ac.ebi.spot.gwas.common.projection.MappingProjection;

import java.util.List;


@Repository
public interface LocusRepository extends JpaRepository<Locus, Long> {

    @Query("select locus.id as locusId, locus.association.id as associationId" +

            " FROM Locus locus" +
            " JOIN locus.association association " +
            " WHERE locus.association.id in :ids ")
    List<MappingProjection> findUsingAssociationIds(@Param("ids") List<Long> ids);

}
