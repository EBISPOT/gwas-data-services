package uk.ac.ebi.spot.gwas.submission.service.impl;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.deposition.domain.User;
import uk.ac.ebi.spot.gwas.submission.mongo.repository.UserRepository;
import uk.ac.ebi.spot.gwas.submission.service.UserDetailsService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElse(null);
    }
}
