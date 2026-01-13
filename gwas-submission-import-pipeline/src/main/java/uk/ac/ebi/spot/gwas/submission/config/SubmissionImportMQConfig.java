package uk.ac.ebi.spot.gwas.submission.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.spot.gwas.deposition.config.SubmissionImportMQConfigProperties;

@Configuration
public class SubmissionImportMQConfig {

    @Autowired
    SubmissionImportMQConfigProperties submissionImportMQConfigProperties;

    @Bean
    Queue submissionImportQueue() {
        return new Queue(submissionImportMQConfigProperties.getSubmissionImportQueueName());
    }

    @Bean
    DirectExchange submissionImportExchange() {
        return new DirectExchange(submissionImportMQConfigProperties.getSubmissionImportExchangeName());
    }

    @Bean
    Binding submissionImportBinding(Queue submissionImportQueue, DirectExchange submissionImportExchange) {
        return BindingBuilder.bind(submissionImportQueue).to(submissionImportExchange).with(submissionImportMQConfigProperties.getSubmissionImportRoutingKey());
    }


}
