package uk.ac.ebi.spot.gwas.rabbitmq.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.deposition.dto.curation.DiseaseTraitRabbitMessage;
import uk.ac.ebi.spot.gwas.model.DiseaseTrait;
import uk.ac.ebi.spot.gwas.rabbitmq.dto.DiseaseTraitRabbitMessageAssembler;
import uk.ac.ebi.spot.gwas.rabbitmq.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.gwas.rabbitmq.repository.StudyRepository;
import uk.ac.ebi.spot.gwas.rabbitmq.service.DiseaseTraitImportService;

import java.util.Optional;

@Service
public class DiseaseTraitImportServiceImpl implements DiseaseTraitImportService {

    @Autowired
    DiseaseTraitRabbitMessageAssembler diseaseTraitRabbitMessageAssembler;

    @Autowired
    DiseaseTraitRepository diseaseTraitRepository;

    @Autowired
    StudyRepository studyRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void importDiseaseTrait(DiseaseTraitRabbitMessage diseaseTraitRabbitMessage) {
        if(diseaseTraitRabbitMessage.getOperation().equals("insert")) {
            DiseaseTrait diseaseTrait = diseaseTraitRabbitMessageAssembler.disassemble(diseaseTraitRabbitMessage);
            diseaseTraitRepository.save(diseaseTrait);
        } else if(diseaseTraitRabbitMessage.getOperation().equals("update")) {
            DiseaseTrait diseaseTrait =   getDiseaseTrait(diseaseTraitRabbitMessage.getMongoSeqId());
            diseaseTrait.setTrait(Optional.ofNullable(diseaseTraitRabbitMessage.getTrait()).orElse(diseaseTrait.getTrait()));
            diseaseTraitRepository.save(diseaseTrait);
        } else if(diseaseTraitRabbitMessage.getOperation().equals("delete")) {
            DiseaseTrait diseaseTrait  = getDiseaseTrait(diseaseTraitRabbitMessage.getMongoSeqId());
            if(diseaseTrait != null) {
                studyRepository.findByDiseaseTraitId(diseaseTrait.getId()).forEach(study -> {
                    study.setDiseaseTrait(null);
                    study.setBackgroundTrait(null);
                    studyRepository.save(study);
                });
                diseaseTraitRepository.delete(diseaseTrait);
            }
        }
    }

   private DiseaseTrait getDiseaseTrait(String mongoSeqId) {
        return diseaseTraitRepository.findByMongoSeqId(mongoSeqId).orElse(null);
    }
}
