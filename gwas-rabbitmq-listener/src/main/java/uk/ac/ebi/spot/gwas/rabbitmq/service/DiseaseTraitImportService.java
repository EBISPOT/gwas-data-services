package uk.ac.ebi.spot.gwas.rabbitmq.service;

import uk.ac.ebi.spot.gwas.deposition.dto.curation.DiseaseTraitRabbitMessage;

public interface DiseaseTraitImportService {

     void importDiseaseTrait(DiseaseTraitRabbitMessage traitRabbitMessage);
}
