package uk.ac.ebi.spot.gwas.rabbitmq.dto;

import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.deposition.dto.curation.EfoTraitRabbitMessage;
import uk.ac.ebi.spot.gwas.model.EfoTrait;

@Component
public class EFOTraitRabbitMessageAssembler {

    public EfoTrait disassemble(EfoTraitRabbitMessage efoTraitRabbitMessage) {
        EfoTrait efoTrait = new EfoTrait();
        efoTrait.setTrait(efoTraitRabbitMessage.getTrait());
        efoTrait.setShortForm(efoTraitRabbitMessage.getShortForm());
        efoTrait.setMongoSeqId(efoTraitRabbitMessage.getMongoSeqId());
        efoTrait.setUri(efoTraitRabbitMessage.getUri());
        return efoTrait;
    }
}
