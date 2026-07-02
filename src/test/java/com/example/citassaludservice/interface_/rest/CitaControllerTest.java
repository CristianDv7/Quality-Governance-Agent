package com.example.citassaludservice.interface_.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class CitaControllerTest {

    @Autowired WebApplicationContext context;
    final ObjectMapper objectMapper = new ObjectMapper();

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private static final String PACIENTE_ID = "b1b2c3d4-0001-0001-0001-000000000001";
    private static final String DISPONIBLE_ID = "c1000001-0001-0001-0001-000000000007";
    private static final String OCUPADA_ID = "e1000002-0001-0001-0001-000000000099";

    @Test
    void given_franja_disponible_when_post_citas_then_returns_201_confirmada() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "pacienteId", PACIENTE_ID,
                "disponibilidadId", DISPONIBLE_ID
        ));
        mockMvc.perform(post("/api/v1/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"))
                .andExpect(jsonPath("$.canalCreacion").value("ONLINE"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @Sql(scripts = "/db/test-data-us2.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void given_franja_ocupada_when_post_citas_then_returns_409_franja_no_disponible() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "pacienteId", PACIENTE_ID,
                "disponibilidadId", OCUPADA_ID
        ));
        mockMvc.perform(post("/api/v1/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("FRANJA_NO_DISPONIBLE"))
                .andExpect(jsonPath("$.mensaje").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void given_existing_cita_when_get_citas_paciente_then_returns_200_with_cita() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "pacienteId", PACIENTE_ID,
                "disponibilidadId", "c1000001-0001-0001-0001-000000000008"
        ));
        mockMvc.perform(post("/api/v1/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/pacientes/{pacienteId}/citas", PACIENTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
