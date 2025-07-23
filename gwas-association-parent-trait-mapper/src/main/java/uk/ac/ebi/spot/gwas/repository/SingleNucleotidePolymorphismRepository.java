package uk.ac.ebi.spot.gwas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.ac.ebi.spot.gwas.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.gwas.rest.projection.AssociationGeneProjection;
import uk.ac.ebi.spot.gwas.rest.projection.SnpGeneProjection;

import java.util.List;

public interface SingleNucleotidePolymorphismRepository extends JpaRepository<SingleNucleotidePolymorphism, Long> {

    @Query("select snp.id as snpId, g.id as geneId from SingleNucleotidePolymorphism snp " +
            "join snp.genomicContexts as gc " +
            "join gc.gene as g " +
            "join gc.location loc " +
            "where snp.id = :snpId " +
            "and  length(loc.chromosomeName) < 3 " +
            "and gc.source = :source " +
            "and gc.isIntergenic = false ")
    List<SnpGeneProjection> findOverLappingGenes(Long snpId, String source);

    @Query("select snp.id as snpId, g.id as geneId from SingleNucleotidePolymorphism snp " +
            "join snp.genomicContexts as gc " +
            "join gc.gene as g " +
            "join gc.location loc " +
            "where snp.id = :snpId " +
            "and  length(loc.chromosomeName) < 3 "+
            "and gc.source = :source "+
            "and gc.isClosestGene = true " +
            "and (gc.isUpstream = true OR gc.isDownstream = true) ")
    List<SnpGeneProjection> findUpDownStreamGenes(Long snpId, String source);

}
