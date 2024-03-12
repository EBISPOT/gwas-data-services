package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.data.copy.model.SecureUser;

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
