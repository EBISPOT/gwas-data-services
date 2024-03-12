package uk.ac.ebi.spot.gwas.data.copy.table.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.data.copy.table.model.EnsemblRestcallHistory;

import java.util.Collection;

/**
 * Created by Cinzia on 30/01/2017.
 *
 * @author cinzia
 *         <p>
 *         Repository accessing EnsemblRestCallHistory entity object
 *         This table stores the Ensembl requests and the relative responses.
 */


public interface EnsemblRestcallHistoryRepository extends JpaRepository<EnsemblRestcallHistory, Long> {

    Collection<EnsemblRestcallHistory> findByRequestTypeAndEnsemblParamAndEnsemblVersion(String requestType, String ensemblParam, String ensemblVersion);

}
