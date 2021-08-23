package uk.ac.ebi.spot.gwas.service.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.gwas.model.SecureUser;

@Repository
public interface SecureUserRepository extends JpaRepository<SecureUser, Long> {
    SecureUser findByEmail(String email);
}
