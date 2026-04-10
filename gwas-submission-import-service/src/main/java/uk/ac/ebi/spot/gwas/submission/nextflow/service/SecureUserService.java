package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.model.SecureUser;

public interface SecureUserService {

    SecureUser findByEmail(String email);
}
