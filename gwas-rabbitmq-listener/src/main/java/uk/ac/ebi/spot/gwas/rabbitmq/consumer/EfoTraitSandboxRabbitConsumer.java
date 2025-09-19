package uk.ac.ebi.spot.gwas.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.deposition.dto.curation.EfoTraitRabbitMessage;
import uk.ac.ebi.spot.gwas.deposition.messaging.email.EmailService;
import uk.ac.ebi.spot.gwas.rabbitmq.config.BackendEmailConfig;
import uk.ac.ebi.spot.gwas.rabbitmq.constants.DepositionCurationConstants;
import uk.ac.ebi.spot.gwas.rabbitmq.service.EfoTraitImportService;

@Slf4j
@Component
@Profile({"sandbox-migration", "local"})
public class EfoTraitSandboxRabbitConsumer {

    private final Logger bsubLog = LoggerFactory.getLogger("bsublogger");

    @Autowired
    EfoTraitImportService efoTraitImportService;

    @Autowired
    EmailService emailService;

    @Autowired
    BackendEmailConfig backendEmailConfig;


    @RabbitListener(queues = {DepositionCurationConstants.QUEUE_EFOTRAIT_SANDBOX})
    public void listen(EfoTraitRabbitMessage efoTraitRabbitMessage) {
        try {
            log.info("Consuming message for efoTraitRabbitMessage : {}", efoTraitRabbitMessage.getShortForm());
            efoTraitImportService.importEfoTrait(efoTraitRabbitMessage);
        } catch (Exception ex) {
            log.error("Error in consuming message for efoTraitRabbitMessage" + ex.getMessage(), ex);
            String content = "";
            String subject = "";
            if (efoTraitRabbitMessage.getShortForm() != null) {
                content = String.format("Efo import failed for %s %s %s", efoTraitRabbitMessage.getShortForm(), ex.getMessage(), ex);
                subject = String.format("Efo Rabbit consumer - Error encountered import failed for %s", efoTraitRabbitMessage.getShortForm());
            } else {
                content = String.format("Efo import failed %s %s", ex.getMessage(), ex);
                subject = "EFO Rabbit consumer - Error encountered import failed";
            }

            for (String emailAddress : backendEmailConfig.getToAddress().split(",")) {
                if (efoTraitRabbitMessage.getShortForm() != null) {
                    log.info("EFO import failed for {}", efoTraitRabbitMessage.getShortForm());
                    bsubLog.info("GWAS Rabbit consumer - Error encountered import failed for {}", efoTraitRabbitMessage.getShortForm());
                }
                emailService.sendMessage(emailAddress, subject, content, false);
            }
        }
    }
}
