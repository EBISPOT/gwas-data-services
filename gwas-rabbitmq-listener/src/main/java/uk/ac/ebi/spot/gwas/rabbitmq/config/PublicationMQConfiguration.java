package uk.ac.ebi.spot.gwas.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.spot.gwas.deposition.config.PublicationMQConfigProperties;
import uk.ac.ebi.spot.gwas.deposition.dto.curation.MetadataYmlUpdate;
import uk.ac.ebi.spot.gwas.deposition.dto.curation.PublicationRabbitMessage;
import uk.ac.ebi.spot.gwas.deposition.dto.curation.StudyRabbitMessage;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PublicationMQConfiguration {

    @Autowired
    PublicationMQConfigProperties publicationMQConfigProperties;

    @Bean
    Queue publicationQueue(){
        return new Queue(publicationMQConfigProperties.getPublicationQueueName(), true);
    }

    @Bean
    DirectExchange publicationExchange(){
        return new DirectExchange(publicationMQConfigProperties.getPublicationExchangeName());
    }

    @Bean
    Binding publicationBinding(Queue publicationQueue, DirectExchange publicationExchange) {
        return BindingBuilder.bind(publicationQueue).to(publicationExchange).with(publicationMQConfigProperties.getPublicationRoutingKey());
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
        idClassMapping.put("publicationRabbitMessage", PublicationRabbitMessage.class);
        classMapper.setIdClassMapping(idClassMapping);
        classMapper.setDefaultType(Map.class);
        classMapper.setTrustedPackages("*");
        return classMapper;
    }
}
