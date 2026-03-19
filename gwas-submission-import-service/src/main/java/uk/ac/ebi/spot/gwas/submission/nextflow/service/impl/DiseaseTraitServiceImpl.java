package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.deposition.domain.DiseaseTrait;
import uk.ac.ebi.spot.gwas.submission.nextflow.mongo.repository.DiseaseTraitMongoRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.DiseaseTraitService;

@Slf4j
@Service
public class DiseaseTraitServiceImpl implements DiseaseTraitService {

    DiseaseTraitMongoRepository diseaseTraitMongoRepository;

    DiseaseTraitRepository diseaseTraitRepository;

    public DiseaseTraitServiceImpl(DiseaseTraitMongoRepository diseaseTraitMongoRepository,
                                   DiseaseTraitRepository diseaseTraitRepository) {
        this.diseaseTraitMongoRepository = diseaseTraitMongoRepository;
        this.diseaseTraitRepository = diseaseTraitRepository;
    }

    public DiseaseTrait getMongoDiseaseTrait(String traitId) {
      return diseaseTraitMongoRepository.findById(traitId).orElse(null);
    }

    @Transactional(readOnly = true)
    public uk.ac.ebi.spot.gwas.model.DiseaseTrait getDiseaseTrait(String trait) {
        log.info("Disease Trait is {}", trait);
       return diseaseTraitRepository.findByTrait(trait).orElse(null);
    }
}
