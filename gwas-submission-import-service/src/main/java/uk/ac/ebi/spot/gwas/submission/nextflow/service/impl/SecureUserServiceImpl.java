package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.SecureUser;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.SecureUserRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.SecureUserService;

@Service
public class SecureUserServiceImpl implements SecureUserService {

    SecureUserRepository  secureUserRepository;

    public SecureUserServiceImpl(SecureUserRepository secureUserRepository) {
        this.secureUserRepository = secureUserRepository;
    }

   public SecureUser findByEmail(String email) {
       return secureUserRepository.findByEmailIgnoreCase(email).orElse(null);
    }

}
