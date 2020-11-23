package com.github.muirandy.docs.yatspec.distributed.example;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class KafkaResource implements QuarkusTestResourceLifecycleManager {

    static final String KAFKA_HOSTNAME = "kafka.bootstrap.server";
    static final String KAFKA_PORT = "kafka.bootstrap.port";

    private static final int WORKING_KAFKA_BROKER_PORT = 9093;

    private final KafkaContainer kafka = new KafkaContainer()
            .withExposedPorts(WORKING_KAFKA_BROKER_PORT);

    @Override
    public Map<String, String> start() {
        kafka.start();
        kafka.waitingFor(Wait.forLogMessage(".*Successfully submitted metrics to Confluent via secure endpoint.*", 1));

        return Map.of(
                KAFKA_HOSTNAME, kafka.getHost(),
                KAFKA_PORT, "" + kafka.getMappedPort(WORKING_KAFKA_BROKER_PORT)
        );
    }

    @Override
    public void stop() {
        kafka.close();
    }
}