package uk.ac.ebi.spot.gwas.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.gwas.common.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.gwas.common.projection.MappingProjection;

import java.util.Collection;
import java.util.List;


@Repository
public interface SingleNucleotidePolymorphismRepository extends JpaRepository<SingleNucleotidePolymorphism, Long> {

    SingleNucleotidePolymorphism findByRsId(@Param("rsId") String rsId);

    SingleNucleotidePolymorphism findByRsIdIgnoreCase(@Param("rsId") String rsId);

    Collection<SingleNucleotidePolymorphism> findByRiskAllelesLociId(Long locusId);

    @Query("select snp.rsId as snpRsid, loci.id as locusId" +
            " FROM SingleNucleotidePolymorphism as snp" +

            " JOIN snp.riskAlleles as riskAlleles " +
            " JOIN riskAlleles.loci as loci " +
            " WHERE loci.id in :ids")
    List<MappingProjection> findUsingRiskAllelesLociIds(@Param("ids") List<Long> ids);

    @Query("select new SingleNucleotidePolymorphism(s.id) from SingleNucleotidePolymorphism s join s.locations loc " +
            "where loc.id = :locationId")
    List<SingleNucleotidePolymorphism> findIdsByLocationId(Long locationId);

}

