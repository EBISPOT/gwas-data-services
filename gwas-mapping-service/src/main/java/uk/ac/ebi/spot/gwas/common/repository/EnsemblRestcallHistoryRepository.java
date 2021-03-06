package uk.ac.ebi.spot.gwas.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.gwas.common.model.EnsemblRestcallHistory;

import java.util.Collection;


@Repository
public interface EnsemblRestcallHistoryRepository extends JpaRepository<EnsemblRestcallHistory, Long> {

    Collection<EnsemblRestcallHistory> findByRequestTypeAndEnsemblParamAndEnsemblVersion(String requestType, String ensemblParam, String ensemblVersion);

}
