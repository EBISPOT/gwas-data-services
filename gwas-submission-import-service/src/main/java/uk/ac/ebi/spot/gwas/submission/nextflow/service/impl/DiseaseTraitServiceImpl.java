package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.deposition.domain.DiseaseTrait;
import uk.ac.ebi.spot.gwas.submission.nextflow.mongo.repository.DiseaseTraitMongoRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.DiseaseTraitService;

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

    public uk.ac.ebi.spot.gwas.model.DiseaseTrait getDiseaseTrait(String trait) {
       return diseaseTraitRepository.findByTrait(trait).orElse(null);
    }
}
