package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.ac.ebi.spot.gwas.model.GenomicContext;

import java.util.Collection;
import java.util.List;

/**
 * Created by Laurent on 12/05/15.
 * <p>
 * author lgil
 * <p>
 * Repository accessing GenomicContext entity object
 */

public interface GenomicContextRepository extends JpaRepository<GenomicContext, Long> {

    Collection<GenomicContext> findBySnpId(Long snpId);

    List<GenomicContext> findByLocationId(Long locationId);

    @Query("select new GenomicContext (g.id) from GenomicContext g join g.location loc where loc.id = :locationId")
    List<GenomicContext> findIdsByLocationId(Long locationId);

}
