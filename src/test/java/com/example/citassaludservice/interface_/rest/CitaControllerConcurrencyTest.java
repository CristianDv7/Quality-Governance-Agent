package com.example.citassaludservice.interface_.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class CitaControllerConcurrencyTest {

    @Autowired WebApplicationContext context;
    final ObjectMapper objectMapper = new ObjectMapper();
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private static final String PACIENTE_1 = "b1b2c3d4-0001-0001-0001-000000000001";
    private static final String PACIENTE_2 = "b1b2c3d4-0002-0002-0002-000000000002";
    private static final String DISPONIBLE_ID = "c1000001-0001-0001-0001-000000000009";

    @Test
    void given_two_concurrent_requests_for_same_franja_when_both_post_then_only_one_wins()
            throws Exception {
        int threads = 2;
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch go = new CountDownLatch(1);
        AtomicInteger created = new AtomicInteger(0);
        AtomicInteger conflict = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        String[] pacientes = {PACIENTE_1, PACIENTE_2};
        Future<?>[] futures = new Future[threads];

        for (int i = 0; i < threads; i++) {
            final String pacienteId = pacientes[i];
            futures[i] = executor.submit(() -> {
                try {
                    ready.countDown();
                    go.await();
                    String body = objectMapper.writeValueAsString(Map.of(
                            "pacienteId", pacienteId,
                            "disponibilidadId", DISPONIBLE_ID
                    ));
                    MvcResult result = mockMvc.perform(
                            post("/api/v1/citas")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body)
                    ).andReturn();
                    int status = result.getResponse().getStatus();
                    if (status == 201) created.incrementAndGet();
                    else if (status == 409) conflict.incrementAndGet();
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        ready.await();
        go.countDown();

        for (Future<?> f : futures) f.get(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(created.get()).isEqualTo(1);
        assertThat(conflict.get()).isEqualTo(1);
    }
}
