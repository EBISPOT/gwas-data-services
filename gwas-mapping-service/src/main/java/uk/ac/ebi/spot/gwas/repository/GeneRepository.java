package uk.ac.ebi.spot.gwas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.gwas.model.Gene;
import uk.ac.ebi.spot.gwas.projection.MappingProjection;

import java.util.List;


@Repository
public interface GeneRepository extends JpaRepository<Gene, Long> {

    @Query("select gene.geneName as geneName, association.id as associationId, locus.id as locusId" +
            " FROM Gene as gene" +

            " JOIN gene.authorReportedFromLoci as locus " +
            " JOIN locus.association as association " +
            " WHERE association.id in :ids")
    List<MappingProjection> findUsingAssociationIds(@Param("ids") List<Long> ids);

}
