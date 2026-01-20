package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.Curator;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.CuratorRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.CuratorService;

@Slf4j
@Service
public class CuratorServiceImpl implements CuratorService {

    CuratorRepository curatorRepository;

    public CuratorServiceImpl(CuratorRepository curatorRepository ) {
        this.curatorRepository = curatorRepository;
    }

    @Transactional(readOnly = true)
    public Curator findByEmail(String email) {
        log.info("Curator Email {}", email);
        return curatorRepository.findByEmail(email).orElse(null);
    }

    public Curator findByLastName(String lastName) {
      return curatorRepository.findByLastNameIgnoreCase(lastName).orElse(null);
   }
}
