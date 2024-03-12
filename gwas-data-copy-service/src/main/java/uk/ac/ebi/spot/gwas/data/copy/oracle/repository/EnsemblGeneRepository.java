package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.data.copy.model.EnsemblGene;

/**
 * Created by emma on 21/07/2015.
 *
 * @author emma
 *         <p>
 *         Repository accessing Ensembl Gene entity object
 */

public interface EnsemblGeneRepository extends JpaRepository<EnsemblGene, Long> {

    EnsemblGene findByEnsemblGeneId(String ensemblGeneId);

}
