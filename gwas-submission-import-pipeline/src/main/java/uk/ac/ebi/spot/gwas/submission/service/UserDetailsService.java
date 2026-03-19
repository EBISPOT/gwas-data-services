package uk.ac.ebi.spot.gwas.submission.service;

import uk.ac.ebi.spot.gwas.deposition.domain.User;

public interface UserDetailsService {

    User findUserByEmail(String email);
}
