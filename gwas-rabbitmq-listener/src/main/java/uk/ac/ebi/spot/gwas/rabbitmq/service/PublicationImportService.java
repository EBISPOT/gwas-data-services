package uk.ac.ebi.spot.gwas.rabbitmq.service;

import uk.ac.ebi.spot.gwas.deposition.dto.curation.PublicationRabbitMessage;

public interface PublicationImportService {

    void importPublication(PublicationRabbitMessage publicationRabbitMessage);
}
