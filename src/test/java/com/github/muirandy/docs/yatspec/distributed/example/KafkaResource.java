package com.github.muirandy.docs.yatspec.distributed.example;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class KafkaResource implements QuarkusTestResourceLifecycleManager {

    static final String KAFKA_HOSTNAME = "kafka.bootstrap.server";
    static final String KAFKA_INTERNAL_DOCKER_HOSTNAME = "kafka.bootstrap.internal.server";
    static final String KAFKA_INTERNAL_PORT = "kafka.bootstrap.internal.port";
    static final String KAFKA_EXTERNAL_PORT = "kafka.bootstrap.external.port";
    static final String KAFKA_IP = "kafka.bootstrap.ip";

    private static final int INTERNAL_KAFKA_BROKER_PORT = 9092;
    private static final int EXTERNAL_KAFKA_BROKER_PORT = 9093;

    private final KafkaContainer kafka = new KafkaContainer()
            .withExposedPorts(INTERNAL_KAFKA_BROKER_PORT, EXTERNAL_KAFKA_BROKER_PORT)
            .withNetworkAliases("kafka")
            .withNetwork(Network.SHARED);

    @Override
    public Map<String, String> start() {
        kafka.start();
        kafka.waitingFor(Wait.forLogMessage(".*Successfully submitted metrics to Confluent via secure endpoint.*", 1));

        return Map.of(
                KAFKA_HOSTNAME, kafka.getBootstrapServers(),
                KAFKA_INTERNAL_DOCKER_HOSTNAME, kafka.getContainerName(),
                KAFKA_IP, kafka.getContainerIpAddress(),
                KAFKA_INTERNAL_PORT, "" + INTERNAL_KAFKA_BROKER_PORT,
                KAFKA_EXTERNAL_PORT, "" + kafka.getMappedPort(EXTERNAL_KAFKA_BROKER_PORT)
        );
    }

    @Override
    public void stop() {
        kafka.close();
    }
}