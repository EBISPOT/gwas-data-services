package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.ac.ebi.spot.gwas.model.SingleNucleotidePolymorphism;

import java.util.Collection;
import java.util.List;


/**
 * Created by emma on 21/11/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing Single Nucleotide Polymorphism entity objectls
 */


public interface SingleNucleotidePolymorphismRepository extends JpaRepository<SingleNucleotidePolymorphism, Long> {
    SingleNucleotidePolymorphism findByRsId(@Param("rsId") String rsId);

    SingleNucleotidePolymorphism findByRsIdIgnoreCase(@Param("rsId") String rsId);

    Collection<SingleNucleotidePolymorphism> findByRiskAllelesLociAssociationStudyId(Long studyId);

    Collection<SingleNucleotidePolymorphism> findByRiskAllelesLociAssociationId(Long associationId);

    Collection<SingleNucleotidePolymorphism> findByRiskAllelesLociAssociationStudyDiseaseTraitId(Long traitId);

    List<SingleNucleotidePolymorphism> findByLocationsId(Long locationId);


    @Query("select new SingleNucleotidePolymorphism(s.id) from SingleNucleotidePolymorphism s join s.locations loc " +
            "where loc.id = :locationId")
    List<SingleNucleotidePolymorphism> findIdsByLocationId(Long locationId);

    List<SingleNucleotidePolymorphism> findByLocationsChromosomePosition(@Param("bpLocation") int chromosomePosition);

//    List<SingleNucleotidePolymorphism> findByLocationsChromosomeNameAndLocationsChromosomePositionBetween(@Param("chrom") String chromosomeName, @Param("bpStart") int start, @Param("bpEnd") int end);

    Page<SingleNucleotidePolymorphism> findByLocationsChromosomeNameAndLocationsChromosomePositionBetween(@Param("chrom") String chromosomeName, @Param("bpStart") int start, @Param("bpEnd") int end, Pageable pageable);

    Collection<SingleNucleotidePolymorphism> findByRiskAllelesLociId(Long locusId);
}

