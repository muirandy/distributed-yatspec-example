package com.github.muirandy.docs.yatspec.distributed.example;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class ReverseResourceClient {
    private final Client client = ClientBuilder.newClient();
    private String stubHost;
    private String stubPort;

    void configure(String host, String port) {
        stubHost = host;
        stubPort = port;
    }

    String reverse(String s) {
        Response response = client.target(calculateUrl())
                                  .request(MediaType.TEXT_PLAIN)
                                  .post(Entity.entity(s, MediaType.TEXT_PLAIN));

        return response.readEntity(String.class);
    }

    private String calculateUrl() {
        return "http://" + stubHost + ":" + stubPort + "/reverse";
    }
}
