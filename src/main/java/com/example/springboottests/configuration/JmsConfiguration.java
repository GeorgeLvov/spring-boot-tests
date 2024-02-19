package com.example.springboottests.configuration;

import com.ibm.mq.jakarta.jms.MQConnectionFactory;
import com.ibm.msg.client.jakarta.jms.JmsConstants;
import com.ibm.msg.client.jakarta.wmq.common.CommonConstants;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

import java.time.Duration;

/**
 * Configuration class for JMS (Java Message Service) setup.
 * This class provides configuration for connecting to the IBM MQ,
 * defining the connection factory and JMS listener container factory.
 *
 * @author Georgii Lvov
 */
@Slf4j
@Configuration
@EnableJms
public class JmsConfiguration {

    @Value("${queue.host}")
    private String host;

    @Value("${queue.port}")
    private int port;

    @Value("${queue.channel}")
    private String channel;

    @Value("${queue.manager}")
    private String manager;

    @Value("${queue.ssl.cipher_suite}")
    private String cipherSuite;

    @Bean
    public ConnectionFactory connectionFactory() {
        MQConnectionFactory factory = new MQConnectionFactory();

        factory.setHostName(host);
        try {
            factory.setPort(port);
            factory.setQueueManager(manager);
            factory.setChannel(channel);
            factory.setTransportType(CommonConstants.WMQ_CM_CLIENT);
            factory.setCCSID(JmsConstants.CCSID_UTF8);

            if (!cipherSuite.isEmpty())
                factory.setSSLCipherSuite(cipherSuite);

            factory.setPollingInterval((int) Duration.ofMinutes(1).toMillis());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return factory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory containerFactory = new DefaultJmsListenerContainerFactory();

        containerFactory.setConnectionFactory(connectionFactory());
        containerFactory.setSessionAcknowledgeMode(Session.SESSION_TRANSACTED);
        containerFactory.setSessionTransacted(true);

        return containerFactory;
    }
}
