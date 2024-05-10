package uk.ac.ebi.spot.gwas.rabbitmq.consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.deposition.dto.curation.PublicationRabbitMessage;
import uk.ac.ebi.spot.gwas.rabbitmq.constants.DepositionCurationConstants;
import uk.ac.ebi.spot.gwas.rabbitmq.service.PublicationImportService;

/**
This Consumer has been written for debugging purpose ,
In real scenario Python consumes the message for the
metadata Yaml hence we don't need to have this consumer
**/




@Component
public class PublicationRabbitConsumer {

    private static final Logger log = LoggerFactory.getLogger(PublicationRabbitConsumer.class);

    @Autowired
    PublicationImportService publicationImportService;

    @RabbitListener(queues = {DepositionCurationConstants.QUEUE_PUBLICATION_SANDBOX} )
    public void listen(PublicationRabbitMessage publicationRabbitMessage) {
        try {
            log.info("Consuming message for publicationRabbitMessage : {}",publicationRabbitMessage);
            publicationImportService.importPublication(publicationRabbitMessage);

        } catch(Exception ex) {
            log.error("Error in consuming message for Publication Rabbit Messsage"+ex.getMessage(),ex);
        }
    }
}


