package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.EfoTrait;
import uk.ac.ebi.spot.gwas.submission.nextflow.mongo.repository.EfoTraitMongoRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.EfoTraitRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.EfoTraitService;

@Service
public class EfoTraitServiceImpl implements EfoTraitService {

    EfoTraitRepository efoTraitRepository;

    EfoTraitMongoRepository efoTraitMongoRepository;

    public EfoTraitServiceImpl(EfoTraitRepository efoTraitRepository,
                               EfoTraitMongoRepository efoTraitMongoRepository) {
        this.efoTraitRepository = efoTraitRepository;
        this.efoTraitMongoRepository = efoTraitMongoRepository;
    }


    public EfoTrait findByShortForm(String shortForm) {
       return efoTraitRepository.findByShortForm(shortForm).orElse(null);
   }

   public uk.ac.ebi.spot.gwas.deposition.domain.EfoTrait findByMongoId(String mongoId) {
        return efoTraitMongoRepository.findById(mongoId).orElse(null);
   }


}
