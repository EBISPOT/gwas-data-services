package uk.ac.ebi.spot.gwas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.gwas.model.EnsemblGene;

@Repository
public interface EnsemblGeneRepository extends JpaRepository<EnsemblGene, Long> {

    EnsemblGene findByEnsemblGeneId(String ensemblGeneId);

}
