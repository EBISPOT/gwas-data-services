package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.Platform;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.PlatformRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.PlatformService;

@Service
public class PlatformServiceImpl implements PlatformService {

    PlatformRepository platformRepository;

    public PlatformServiceImpl(PlatformRepository platformRepository) {
        this.platformRepository = platformRepository;
    }

   public  Platform findByManufacturer(String manufacturer) {
        return platformRepository.findByManufacturer(manufacturer).orElse(null);
   }

}
