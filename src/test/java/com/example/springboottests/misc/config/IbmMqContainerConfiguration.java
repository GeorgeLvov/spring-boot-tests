package com.example.springboottests.misc.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Configuration class for setting up an IBM MQ container for integration tests.
 * <p>
 * This class provides a bean for creating and configuring an IBM MQ Docker container
 * using Testcontainers, allowing integration tests to interact with an IBM MQ instance.
 * <p>
 * Note:
 * As of the latest information, <i>ibmcom/mq</i> image isn't available for Mac with M1 chip (Apple Silicon).
 * To build an image for it see <a href="https://community.ibm.com/community/user/integration/blogs/richard-coppen/2023/06/30/ibm-mq-9330-container-image-now-available-for-appl">this guide</a>
 *
 * @author Georgii Lvov
 */
@TestConfiguration(proxyBeanMethods = false)
public class IbmMqContainerConfiguration {

    // doesn't work with Mac M1 (Apple Silicon)
    private static final String IBM_MQ_IMAGE_NAME  = "ibmcom/mq";

    /**
     * Creates a {@link GenericContainer} bean for running an IBM MQ Docker container.
     * <p>
     * The IBM MQ Docker container is configured with the required environment variables
     * and exposed ports for integration testing purposes.
     *
     * @param registry the DynamicPropertyRegistry for registering dynamic properties
     * @return a {@link GenericContainer} instance representing the IBM MQ container
     */
    @Bean
    @SuppressWarnings("resource")
    public GenericContainer<?> ibmMqContainer(DynamicPropertyRegistry registry) {
        GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse(IBM_MQ_IMAGE_NAME))
                .withEnv("LICENSE", "accept")
                .withEnv("MQ_QMGR_NAME", "QM1")
                .withExposedPorts(1414);

        registry.add("queue.host", container::getHost);
        registry.add("queue.port", container::getFirstMappedPort);

        return container;
    }
}
