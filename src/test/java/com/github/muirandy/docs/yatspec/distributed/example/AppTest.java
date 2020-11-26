package com.github.muirandy.docs.yatspec.distributed.example;

import com.github.muirandy.docs.yatspec.distributed.DiagramLogger;
import com.github.muirandy.docs.yatspec.distributed.SequenceDiagramFacade;
import com.github.muirandy.docs.yatspec.distributed.log.kafka.KafkaConfiguration;
import com.github.muirandy.docs.yatspec.distributed.log.kafka.KafkaLogger;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.json.bind.JsonbBuilder;

import static com.github.muirandy.docs.yatspec.distributed.example.KafkaResource.*;
import static com.github.muirandy.docs.yatspec.distributed.example.StubResource.STUB_HOSTNAME;
import static com.github.muirandy.docs.yatspec.distributed.example.StubResource.STUB_PORT;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@QuarkusTestResource(KafkaResource.class)
@QuarkusTestResource(StubResource.class)
public class AppTest {
    private static final String SOURCE_SERVICE_NAME = "App";
    private static final String STUB_SERVICE_NAME = "Stub Dependency";
    private static final String BASE_PATH = "/app";
    private static final String CONFIGURE_APP_PATH = BASE_PATH + "/configure";
    private DiagramLogger diagramLogger;
    private KafkaConfiguration kafkaTestConfiguration;
    private SequenceDiagramFacade sequenceDiagramFacade;
    private String sourceSystem;
    private String thisSystemName;
    private Response response;


    @BeforeEach
    void setup() {
        pointStubAtKafka();
        pointAppAtStub();
        pointTestAtKafka();
        createSequenceDiagramLogger();
        calculateServiceNames();

    }

    private void pointTestAtKafka() {
        kafkaTestConfiguration = createTestKafkaConfiguration();
    }

    private void pointStubAtKafka() {
        KafkaConfiguration kafkaConfiguration = createDockerInternalKafkaConfiguration();
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

    private void pointAppAtStub() {
        StubConfiguration stubConfiguration = createStubConfiguration();
        String input = JsonbBuilder.create().toJson(stubConfiguration);

        given()
                .header("Content-Type", "application/json")
                .when()
                .body(input)
                .post(CONFIGURE_APP_PATH)
                .then()
                .statusCode(204);
    }

    private void createSequenceDiagramLogger() {
        diagramLogger = new KafkaLogger(kafkaTestConfiguration.kafkaHost, kafkaTestConfiguration.kafkaPort);
        sequenceDiagramFacade = new SequenceDiagramFacade(diagramLogger);
    }

    private void calculateServiceNames() {
        sourceSystem = kafkaTestConfiguration.sourceSystemName;
        thisSystemName = kafkaTestConfiguration.thisSystemName;
    }

    private KafkaConfiguration createDockerInternalKafkaConfiguration() {
        String kafkaHost = useDockerInternalNameForKafka();
        String kafkaExternalPort = System.getProperty(KAFKA_INTERNAL_PORT);
        return new KafkaConfiguration(SOURCE_SERVICE_NAME, STUB_SERVICE_NAME, kafkaHost, Integer.valueOf(kafkaExternalPort));
    }

    private StubConfiguration createStubConfiguration() {
        String stubHost = System.getProperty(STUB_HOSTNAME);
        String stubPort = System.getProperty(STUB_PORT);
        StubConfiguration stubConfiguration = new StubConfiguration(stubHost, stubPort);
        return stubConfiguration;
    }

    private String useDockerInternalNameForKafka() {
        String kafkaBootstrapHost = System.getProperty(KAFKA_HOSTNAME);
        int start = kafkaBootstrapHost.indexOf("://") + 3;
        return kafkaBootstrapHost.substring(0, start) + "kafka";
    }

    private KafkaConfiguration createTestKafkaConfiguration() {
        String kafkaHost = kafkaServerFromHost();
        String kafkaExternalPort = System.getProperty(KAFKA_EXTERNAL_PORT);
        return new KafkaConfiguration(SOURCE_SERVICE_NAME, STUB_SERVICE_NAME, kafkaHost, Integer.valueOf(kafkaExternalPort));
    }

    private String kafkaServerFromHost() {
        String kafkaBootstrapHost = System.getProperty(KAFKA_HOSTNAME);

        if (containsPort(kafkaBootstrapHost)) {
            int i = kafkaBootstrapHost.lastIndexOf(":");
            return kafkaBootstrapHost.substring(0, i);
        }
        return kafkaBootstrapHost;
    }

    private boolean containsPort(String kafkaBootstrapHost) {
        return kafkaBootstrapHost.split(":").length > 2;
    }

    @Test
    void reversesWordsInSentence() {
        whenUserSubmitsSentenceToReverse("The quick brown fox");
        thenResponseIs("ehT kciuq nworb xof");
    }

    private void whenUserSubmitsSentenceToReverse(String sentence) {
        response = given()
                .header("Content-Type", "application/json")
                .when()
                .body(sentence)
                .post("/app/reverse");
    }

    private void thenResponseIs(String sentence) {
        response.then()
                .statusCode(200)
                .body(is(sentence));
    }
}