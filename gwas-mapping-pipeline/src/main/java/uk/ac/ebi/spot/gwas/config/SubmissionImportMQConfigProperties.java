package uk.ac.ebi.spot.gwas.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class SubmissionImportMQConfigProperties {

    @Value("${rabbitmq.submission-import.queue-name:#{NULL}}")
    private String submissionImportQueueName;

    @Value("${rabbitmq.submission-import.queue-name:#{NULL}}")
    private String submissionImportExchangeName;

    @Value("${rabbitmq.submission-import.queue-name:#{NULL}}")
    private String submissionImportRoutingKey;


}
