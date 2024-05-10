package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.SecureRole;

/**
 * Created by emma on 10/02/15.
 *
 * @author emma
 *         <p>
 *         Repository accessing Role entity object
 */

public interface SecureRoleRepository extends JpaRepository<SecureRole, Long> {
}
