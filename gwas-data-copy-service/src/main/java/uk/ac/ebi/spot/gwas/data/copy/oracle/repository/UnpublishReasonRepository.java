package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.ac.ebi.spot.gwas.data.copy.model.UnpublishReason;

/**
 * Created by dwelter on 04/06/15.
 */

public interface UnpublishReasonRepository extends JpaRepository<UnpublishReason, Long> {

    UnpublishReason findByReason(String reason);


}
