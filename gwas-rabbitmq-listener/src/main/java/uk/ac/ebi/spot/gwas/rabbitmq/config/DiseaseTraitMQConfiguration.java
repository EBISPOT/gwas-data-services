package uk.ac.ebi.spot.gwas.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.spot.gwas.deposition.config.DiseaseTraitMQConfigProperties;

@Configuration
public class DiseaseTraitMQConfiguration {

    @Autowired
    DiseaseTraitMQConfigProperties diseaseTraitMQConfigProperties;

    @Bean
    Queue diseaseTraitQueue() {
        return new Queue(diseaseTraitMQConfigProperties.getDiseasetraitQueueName());
    }

    @Bean
    DirectExchange diseaseTraitExchange() {
        return new DirectExchange(diseaseTraitMQConfigProperties.getDiseasetraitExchangeName());
    }

    @Bean
    Binding diseaseTraitBinding(Queue diseaseTraitQueue, DirectExchange diseaseTraitExchange) {
        return BindingBuilder.bind(diseaseTraitQueue).to(diseaseTraitExchange).with(diseaseTraitMQConfigProperties.getDiseasetraitRoutingKey());
    }
}
