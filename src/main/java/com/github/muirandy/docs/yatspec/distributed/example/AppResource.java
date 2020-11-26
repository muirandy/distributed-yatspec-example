package com.github.muirandy.docs.yatspec.distributed.example;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.stream.Collectors;

@Path("/app")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AppResource {

    @Inject
    private ReverseResourceClient reverseResourceClient;

    private String stubHost;
    private String stubPort;

    @Path("/configure")
    @POST
    public void configure(StubConfiguration stubConfiguration) {
        stubHost = stubConfiguration.stubHost;
        stubPort = stubConfiguration.stubPort;

        reverseResourceClient.configure(stubHost, stubPort);
    }

    @Path("reverse")
    @POST
    public String reverse(String sentence) {
        return reverseWords(sentence);
    }

    private String reverseWords(String sentence) {
        return Arrays.stream(sentence.split(" ")).
              map(w -> reverseWord(w))
              .collect(Collectors.joining(" "));
    }

    private String reverseWord(String w) {
        return reverseResourceClient.reverse(w);
    }
}
