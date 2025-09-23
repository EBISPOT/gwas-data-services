package uk.ac.ebi.spot.gwas.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.deposition.dto.curation.DiseaseTraitRabbitMessage;
import uk.ac.ebi.spot.gwas.deposition.messaging.email.EmailService;
import uk.ac.ebi.spot.gwas.rabbitmq.config.BackendEmailConfig;
import uk.ac.ebi.spot.gwas.rabbitmq.constants.DepositionCurationConstants;
import uk.ac.ebi.spot.gwas.rabbitmq.service.DiseaseTraitImportService;

@Slf4j
@Component
@Profile({"sandbox-migration", "local"})
public class DiseaseTraitSandboxRabbitConsumer {

    private final Logger bsubLog = LoggerFactory.getLogger("bsublogger");

    @Autowired
    DiseaseTraitImportService diseaseTraitImportService;


    @Autowired
    EmailService emailService;

    @Autowired
    BackendEmailConfig backendEmailConfig;


    @RabbitListener(queues = {DepositionCurationConstants.QUEUE_DISEASETRAIT_SANDBOX})
    public void listen(DiseaseTraitRabbitMessage diseaseTraitRabbitMessage) {
        try {
            log.info("Consuming message for diseaseTraitRabbitMessage : {}", diseaseTraitRabbitMessage.getTrait());
            diseaseTraitImportService.importDiseaseTrait(diseaseTraitRabbitMessage);
        } catch (Exception ex) {
            log.error("Error in consuming message for diseaseTraitRabbitMessage" + ex.getMessage(), ex);
            String content = "";
            String subject = "";
            if (diseaseTraitRabbitMessage.getTrait() != null) {
                content = String.format("DiseaseTrait import failed for %s %s %s", diseaseTraitRabbitMessage.getTrait(), ex.getMessage(), ex);
                subject = String.format("DiseaseTrait Rabbit consumer - Error encountered import failed for %s", diseaseTraitRabbitMessage.getTrait());
            } else {
                content = String.format("DiseaseTrait import failed %s %s", ex.getMessage(), ex);
                subject = "DiseaseTrait Rabbit consumer - Error encountered import failed";
            }

            for (String emailAddress : backendEmailConfig.getToAddress().split(",")) {
                if (diseaseTraitRabbitMessage.getTrait() != null) {
                    log.info("DiseaseTrait import failed for {}", diseaseTraitRabbitMessage.getTrait());
                    bsubLog.info("DiseaseTrait Rabbit consumer - Error encountered import failed for {}", diseaseTraitRabbitMessage.getTrait());
                }
                emailService.sendMessage(emailAddress, subject, content, false);
            }
        }

    }

}