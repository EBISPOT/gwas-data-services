package uk.ac.ebi.spot.gwas.submission.service.impl;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.deposition.domain.Curator;
import uk.ac.ebi.spot.gwas.submission.mongo.repository.CuratorRepository;
import uk.ac.ebi.spot.gwas.submission.service.CuratorService;

@Service
public class CuratorServiceImpl implements CuratorService {

    CuratorRepository curatorRepository;

    public CuratorServiceImpl(CuratorRepository curatorRepository) {
        this.curatorRepository = curatorRepository;
    }

    public Curator findById(String curatorId) {
        return curatorRepository.findById(curatorId).orElse(null);
    }
}
