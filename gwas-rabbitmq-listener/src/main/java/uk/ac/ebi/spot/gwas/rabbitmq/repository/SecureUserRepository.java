package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.SecureUser;

/**
 * Created by emma on 09/02/15.
 *
 * @author emma
 *         <p>
 *         Repository accessing User entity object
 */

public interface SecureUserRepository extends JpaRepository<SecureUser, Long> {
    SecureUser findByEmail(String email);
}
