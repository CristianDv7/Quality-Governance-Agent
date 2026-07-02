package com.example.citassaludservice.interface_.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class MedicoControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void given_no_filter_when_buscar_medicos_then_returns_all() throws Exception {
        mockMvc.perform(get("/api/v1/medicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void given_especialidad_filter_when_buscar_medicos_then_returns_filtered() throws Exception {
        mockMvc.perform(get("/api/v1/medicos")
                        .param("especialidad", "MEDICINA_GENERAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void given_medico_id_when_obtener_disponibilidad_then_returns_franjas() throws Exception {
        mockMvc.perform(get("/api/v1/medicos/{medicoId}/disponibilidad",
                        "a1b2c3d4-0001-0001-0001-000000000001")
                        .param("fecha", java.time.LocalDate.now().plusDays(6).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void given_medico_id_when_obtener_disponibilidad_different_date_then_returns_empty_or_list() throws Exception {
        mockMvc.perform(get("/api/v1/medicos/{medicoId}/disponibilidad",
                        "a1b2c3d4-0002-0002-0002-000000000002")
                        .param("fecha", java.time.LocalDate.now().plusDays(6).toString()))
                .andExpect(status().isOk());
    }
}
