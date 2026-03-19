package uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.SecureUser;

import java.util.Optional;

public interface SecureUserRepository extends JpaRepository<SecureUser, Long> {

   Optional<SecureUser> findByEmailIgnoreCase(String email);
}
