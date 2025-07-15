package uk.ac.ebi.spot.gwas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.rest.projection.AssociationGeneProjection;

import java.util.List;

public interface AssociationRepository extends JpaRepository<Association, Long> {

   Page<Association> findByEfoTraitsShortForm(String shortForm, Pageable pageable);

   Long countAssociationsByEfoTraitsShortForm(String shortForm);



   @Query(" select a.id as associationId, g.id as geneId from Association as a "+
            "join a.loci as al " +
           "join al.strongestRiskAlleles as ral "+
            "join ral.snp as snp "+
         "join snp.genomicContexts as gc "+
           "join gc.gene as g "+
           "join gc.location loc "+
           "where a.id = :accsnId " +
           "and snp.id = :snpId "+
           "and  length(loc.chromosomeName) < 3 "+
           "and gc.source = :source "+
           "and gc.isIntergenic = false ")
   List<AssociationGeneProjection> findOverLappingGenes(Long accsnId , String source, Long snpId);

    @Query(" select a.id as associationId, g.id as geneId from Association as a "+
            "join a.loci as al " +
            "join al.strongestRiskAlleles as ral "+
            "join ral.snp as snp "+
            "join snp.genomicContexts as gc "+
            "join gc.gene as g "+
            "join gc.location loc "+
            "where a.id =  :accsnId " +
            "and snp.id = :snpId "+
            "and  length(loc.chromosomeName) < 3 "+
            "and gc.source = :source "+
            "and gc.isClosestGene = true " +
            "and (gc.isUpstream = true OR gc.isDownstream = true)")
    List<AssociationGeneProjection> findUpDownStreamGenes(Long accsnId , String source, Long snpId);
}
