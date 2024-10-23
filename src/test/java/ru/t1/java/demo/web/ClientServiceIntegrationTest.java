package ru.t1.java.demo.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.t1.java.demo.model.dto.CheckResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
public class ClientServiceIntegrationTest {

    private WebTestClient client;

    @BeforeEach
    void setup() {
        client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:8088").build();
    }

    @Test
    void checkWebClient_isClientNotBlocked() {

        CheckResponse response = client.post().uri("/bsc-wire-mock/api/client/check")
                .exchange().expectStatus().isOk()
                .expectBody(CheckResponse.class).returnResult().getResponseBody();

        assertNotNull(response);
        assertEquals(false, response.getBlocked());
    }

    @Test
    void checkWebClient_ifCheckResponseNull() {


    }
}
