package com.github.muirandy.docs.yatspec.distributed.example;

import com.googlecode.yatspec.junit.SequenceDiagramExtension;
import com.googlecode.yatspec.state.givenwhenthen.TestState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SequenceDiagramExtension.class)
public class SimpleYatspecTest {

    private final TestState interactions = new TestState();

    @Test
    void yatspecLoggingTest() {
        interactions.log("Request from Client to Server", "hello");
    }
}
