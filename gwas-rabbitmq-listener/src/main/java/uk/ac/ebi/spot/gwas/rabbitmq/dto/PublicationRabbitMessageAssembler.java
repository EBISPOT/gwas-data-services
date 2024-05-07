package uk.ac.ebi.spot.gwas.rabbitmq.dto;

import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.deposition.dto.curation.PublicationRabbitMessage;
import uk.ac.ebi.spot.gwas.model.Publication;
import uk.ac.ebi.spot.gwas.rabbitmq.util.Dateutil;

@Component
public class PublicationRabbitMessageAssembler {



    public Publication disassemble(PublicationRabbitMessage publicationRabbitMessage) {
        Publication publication = new Publication();
        publication.setPubmedId(publicationRabbitMessage.getPmid());
        publication.setTitle(publicationRabbitMessage.getTitle());
        publication.setPublication(publicationRabbitMessage.getJournal());
        publication.setPublicationDate(Dateutil.convertLocalDatetoSqlDate(publicationRabbitMessage.getPublicationDate()));
        return publication;
    }
}
