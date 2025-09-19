package uk.ac.ebi.spot.gwas.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.spot.gwas.deposition.config.EFOTraitMQConfigProperties;

@Configuration
public class EfoTraitMQConfiguration {

    @Autowired
    EFOTraitMQConfigProperties efoTraitMQConfigProperties;

    @Bean
    Queue efoTraitQueue() {return new Queue(efoTraitMQConfigProperties.getEfoTraitQueueName()); }

    @Bean
    DirectExchange efoTraitExchange() {return new DirectExchange(efoTraitMQConfigProperties.getEfoTraitExchangeName()); }

    @Bean
    Binding efoTraitBinding(Queue efoTraitQueue, DirectExchange efoTraitExchange) {
        return BindingBuilder.bind(efoTraitQueue).to(efoTraitExchange).with(efoTraitMQConfigProperties.getEfoTraitExchangeName());
    }
}
