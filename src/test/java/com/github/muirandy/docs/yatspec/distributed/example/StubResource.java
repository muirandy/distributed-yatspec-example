package com.github.muirandy.docs.yatspec.distributed.example;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

public class StubResource implements QuarkusTestResourceLifecycleManager {
    static final String STUB_HOSTNAME = "stub.host";
    static final String STUB_PORT = "stub.port";

    private static final int WORKING_STUB_BROKER_PORT = 8080;

    private final GenericContainer stub = new GenericContainer(DockerImageName.parse("quarkus/stub-example-distributed-yatspec:latest"))
            .withExposedPorts(WORKING_STUB_BROKER_PORT)
            .withNetwork(Network.SHARED);

    @Override
    public Map<String, String> start() {
        stub.start();
        stub.waitingFor(Wait.forLogMessage(".*Successfully submitted metrics to Confluent via secure endpoint.*", 1));

        return Map.of(
                STUB_HOSTNAME, stub.getHost(),
                STUB_PORT, "" + stub.getMappedPort(WORKING_STUB_BROKER_PORT)
        );

    }

    @Override
    public void stop() {
        stub.close();
    }
}
