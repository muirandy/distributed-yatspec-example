package com.github.muirandy.docs.yatspec.distributed.example;

import com.github.muirandy.docs.yatspec.distributed.log.kafka.KafkaConfiguration;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.json.bind.JsonbBuilder;

import static com.github.muirandy.docs.yatspec.distributed.example.KafkaResource.KAFKA_HOSTNAME;
import static com.github.muirandy.docs.yatspec.distributed.example.KafkaResource.KAFKA_PORT;
import static com.github.muirandy.docs.yatspec.distributed.example.StubResource.STUB_HOSTNAME;
import static com.github.muirandy.docs.yatspec.distributed.example.StubResource.STUB_PORT;
import static io.restassured.RestAssured.given;

@QuarkusTest
@QuarkusTestResource(KafkaResource.class)
@QuarkusTestResource(StubResource.class)
public class AppTest {
    private static final String SOURCE_SERVICE_NAME = "App";
    private static final String STUB_SERVICE_NAME = "Stub Dependency";
    private static final String BASE_PATH = "/app";


    @Test
    void name() {
        pointStubAtKafka();

    }

    private void pointStubAtKafka() {
        KafkaConfiguration kafkaConfiguration = createKafkaConfiguration();
        String stubHost = System.getProperty(STUB_HOSTNAME);
        String stubPort = System.getProperty(STUB_PORT);
        String input = JsonbBuilder.create().toJson(kafkaConfiguration);
        given().baseUri("http://" + stubHost + ":" + stubPort)
               .header("Content-Type", "application/json")
               .when()
               .body(input)
               .post("/reverse/configure")
               .then()
               .statusCode(204);
    }

    private KafkaConfiguration createKafkaConfiguration() {
        String kafkaHost = System.getProperty(KAFKA_HOSTNAME);
        String kafkaPort = System.getProperty(KAFKA_PORT);
        return new KafkaConfiguration(SOURCE_SERVICE_NAME, STUB_SERVICE_NAME, kafkaHost, Integer.valueOf(kafkaPort));
    }


}
