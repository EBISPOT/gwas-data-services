package uk.ac.ebi.spot.gwas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.model.MappingProjection;
import uk.ac.ebi.spot.gwas.model.SingleNucleotidePolymorphism;

import java.util.Collection;
import java.util.List;


@Repository
public interface SingleNucleotidePolymorphismRepository extends JpaRepository<SingleNucleotidePolymorphism, Long> {


    @Query(" select new Association (a.id) from Association as a  "+
            "JOIN a.loci as al "+
            "JOIN al.strongestRiskAlleles lra "+
            "JOIN lra.snp as ras "+
            " where ras.rsId = :rsId")
    List<Association> findAssociationsUsingRsId(String rsId);


    Collection<SingleNucleotidePolymorphism> findByRiskAllelesLociId(Long locusId);

}

