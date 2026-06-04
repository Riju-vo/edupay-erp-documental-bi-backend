package bo.edu.uagrm.edupay.adapters.in.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_NAME = "edupay.exchange";
    
    public static final String ERP_CONFIRMED_QUEUE = "erp.payment.confirmed.queue";
    public static final String ERP_REVERSED_QUEUE = "erp.payment.reversed.queue";
    public static final String NEST_PAYMENT_QUEUE = "payment_queue";

    public static final String CONFIRMED_ROUTING_KEY = "payment.confirmed";
    public static final String REVERSED_ROUTING_KEY = "payment.reversed";

    @Bean
    public TopicExchange edupayExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue erpConfirmedQueue() {
        return new Queue(ERP_CONFIRMED_QUEUE, true);
    }

    @Bean
    public Queue erpReversedQueue() {
        return new Queue(ERP_REVERSED_QUEUE, true);
    }

    @Bean
    public Queue nestPaymentQueue() {
        return new Queue(NEST_PAYMENT_QUEUE, true);
    }

    @Bean
    public Binding bindingConfirmed(Queue erpConfirmedQueue, TopicExchange edupayExchange) {
        return BindingBuilder.bind(erpConfirmedQueue).to(edupayExchange).with(CONFIRMED_ROUTING_KEY);
    }

    @Bean
    public Binding bindingReversed(Queue erpReversedQueue, TopicExchange edupayExchange) {
        return BindingBuilder.bind(erpReversedQueue).to(edupayExchange).with(REVERSED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }
}
