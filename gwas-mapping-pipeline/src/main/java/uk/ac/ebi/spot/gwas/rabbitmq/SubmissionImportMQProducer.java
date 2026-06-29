package uk.ac.ebi.spot.gwas.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.config.SubmissionImportMQConfigProperties;



@Slf4j
@Component
public class SubmissionImportMQProducer {

    private RabbitTemplate rabbitTemplate;


    SubmissionImportMQConfigProperties submissionImportMQConfigProperties;

    public SubmissionImportMQProducer(RabbitTemplate rabbitTemplate, SubmissionImportMQConfigProperties submissionImportMQConfigProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.submissionImportMQConfigProperties = submissionImportMQConfigProperties;
    }

    public void send(SubmissionRabbitMessage submissionRabbitMessage) {
        log.info("Submission import messaged received {}", submissionRabbitMessage.getSubmissionId());
        rabbitTemplate.convertAndSend(submissionImportMQConfigProperties.getSubmissionImportExchangeName(), submissionImportMQConfigProperties.getSubmissionImportRoutingKey(), submissionRabbitMessage);
    }
}
