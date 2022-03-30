package uk.ac.ebi.spot.gwas.common.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.gwas.common.model.SecureUser;

@Repository
public interface SecureUserRepository extends JpaRepository<SecureUser, Long> {
    SecureUser findByEmail(String email);
}
