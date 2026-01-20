package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.Platform;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.PlatformRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.PlatformService;

@Service
public class PlatformServiceImpl implements PlatformService {

    PlatformRepository platformRepository;

    public PlatformServiceImpl(PlatformRepository platformRepository) {
        this.platformRepository = platformRepository;
    }

    @Transactional(readOnly = true)
    public  Platform findByManufacturer(String manufacturer) {
        return platformRepository.findByManufacturer(manufacturer).orElse(null);
    }

}
