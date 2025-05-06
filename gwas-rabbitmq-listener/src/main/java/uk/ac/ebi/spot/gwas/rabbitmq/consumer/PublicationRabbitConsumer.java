package uk.ac.ebi.spot.gwas.rabbitmq.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.deposition.dto.curation.PublicationRabbitMessage;
import uk.ac.ebi.spot.gwas.deposition.messaging.email.EmailService;
import uk.ac.ebi.spot.gwas.rabbitmq.config.BackendEmailConfig;
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

    private final Logger bsubLog = LoggerFactory.getLogger("bsublogger");

    @Autowired
    PublicationImportService publicationImportService;

    @Autowired
    EmailService emailService;

    @Autowired
    BackendEmailConfig backendEmailConfig;

    @RabbitListener(queues = {DepositionCurationConstants.QUEUE_PUBLICATION_SANDBOX,
            DepositionCurationConstants.QUEUE_PUBLICATION_PROD} )
    public void listen(PublicationRabbitMessage publicationRabbitMessage) {
        try {
            log.info("Consuming message for publicationRabbitMessage : {}",publicationRabbitMessage.getPmid());
            publicationImportService.importPublication(publicationRabbitMessage);
            //throw new Exception("Dummy error");
        } catch(Exception ex) {
            log.error("Error in consuming message for Publication Rabbit Messsage"+ex.getMessage(),ex);
            String content = "";
            String subject = "";
            if(publicationRabbitMessage.getPmid() != null) {
                 content = String.format("Pmid import failed for %s %s %s", publicationRabbitMessage.getPmid(), ex.getMessage(), ex);
                 subject = String.format("GWAS Rabbit consumer - Error encountered import failed for %s", publicationRabbitMessage.getPmid());
            } else {
                content = String.format("Pmid import failed %s %s", ex.getMessage(), ex);
                subject = "GWAS Rabbit consumer - Error encountered import failed";
            }
            for(String emailAddress : backendEmailConfig.getToAddress().split(",")) {
                if(publicationRabbitMessage.getPmid() != null) {
                    log.info("Pmid import failed for {}", publicationRabbitMessage.getPmid());
                    bsubLog.info("GWAS Rabbit consumer - Error encountered import failed for {}", publicationRabbitMessage.getPmid());
                }
                emailService.sendMessage( emailAddress, subject, content, false );
            }
        }

    }
}


