package uk.ac.ebi.spot.gwas.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.gwas.common.model.EntrezGene;

@Repository
public interface EntrezGeneRepository extends JpaRepository<EntrezGene, Long> {

    EntrezGene findByEntrezGeneId(String entrezGeneId);
}
