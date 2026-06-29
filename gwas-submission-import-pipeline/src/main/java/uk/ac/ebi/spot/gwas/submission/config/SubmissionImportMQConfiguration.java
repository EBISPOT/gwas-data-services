package uk.ac.ebi.spot.gwas.submission.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.spot.gwas.deposition.config.SubmissionImportMQConfigProperties;
import uk.ac.ebi.spot.gwas.deposition.dto.curation.SubmissionRabbitMessage;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SubmissionImportMQConfiguration {

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
    Binding submissionImportBinding() {
        return BindingBuilder.bind(submissionImportQueue()).to(submissionImportExchange()).with(submissionImportMQConfigProperties.getSubmissionImportRoutingKey());
    }


    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter();
        jsonConverter.setClassMapper(classMapper());
        return jsonConverter;
    }

    @Bean
    public DefaultClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("submissionRabbitMessage", SubmissionRabbitMessage.class);
        classMapper.setIdClassMapping(idClassMapping);
        classMapper.setDefaultType(Map.class);
        classMapper.setTrustedPackages("*");
        return classMapper;
    }

}
