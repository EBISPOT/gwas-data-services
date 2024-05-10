package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.MappingMetadata;

/**
 * Created by emma on 28/09/2015.
 *
 * @author emma
 *         <p>
 *         Repository for searching the mapping metadata table
 */

public interface MappingMetadataRepository extends JpaRepository<MappingMetadata, Long> {

}
