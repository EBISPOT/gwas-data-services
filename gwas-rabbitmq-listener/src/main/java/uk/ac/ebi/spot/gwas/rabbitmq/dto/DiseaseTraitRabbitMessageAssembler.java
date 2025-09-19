package uk.ac.ebi.spot.gwas.rabbitmq.dto;

import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.deposition.dto.curation.DiseaseTraitRabbitMessage;
import uk.ac.ebi.spot.gwas.model.DiseaseTrait;

@Component
public class DiseaseTraitRabbitMessageAssembler {

    public DiseaseTrait  disassemble(DiseaseTraitRabbitMessage diseaseTraitRabbitMessage) {
        DiseaseTrait diseaseTrait = new DiseaseTrait();
        diseaseTrait.setTrait(diseaseTraitRabbitMessage.getTrait());
        diseaseTrait.setMongoSeqId(diseaseTraitRabbitMessage.getMongoSeqId());
        return diseaseTrait;
    }

}
