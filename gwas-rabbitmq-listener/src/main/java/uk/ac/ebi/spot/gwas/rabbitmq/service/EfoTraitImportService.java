package uk.ac.ebi.spot.gwas.rabbitmq.service;

import uk.ac.ebi.spot.gwas.deposition.dto.curation.EfoTraitRabbitMessage;

public interface EfoTraitImportService {

    void importEfoTrait(EfoTraitRabbitMessage efoTraitRabbitMessage);
}
