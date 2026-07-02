package com.example.citassaludservice.bdd.steps;

import io.cucumber.java.es.*;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class DisponibilidadSteps {

    final RestTemplate restTemplate = new RestTemplate();

    @LocalServerPort
    int port;

    private ResponseEntity<Map> lastResponse;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Dado("que la franja horaria {string} está ocupada")
    public void franja_ocupada(String disponibilidadId) {
        // OCUPADA fixture loaded via CucumberHooks before @us2 scenarios
    }

    @Cuando("el paciente con id {string} intenta confirmarla")
    public void paciente_intenta_confirmar(String pacienteId) {
        try {
            Map<String, String> body = Map.of(
                    "pacienteId", pacienteId,
                    "disponibilidadId", "e1000002-0001-0001-0001-000000000099"
            );
            lastResponse = restTemplate.postForEntity(baseUrl() + "/api/v1/citas", body, Map.class);
        } catch (HttpClientErrorException e) {
            lastResponse = new ResponseEntity<>(
                    (Map) parseError(e.getResponseBodyAsString()),
                    e.getStatusCode()
            );
        }
    }

    @Entonces("el sistema responde con código de error {string}")
    public void sistema_responde_codigo_error(String codigo) {
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(409);
        assertThat(lastResponse.getBody()).isNotNull();
        assertThat(lastResponse.getBody().get("codigo")).isEqualTo(codigo);
    }

    @Y("el mensaje invita al paciente a elegir otra franja")
    public void mensaje_elegir_otra_franja() {
        assertThat(lastResponse.getBody()).containsKey("mensaje");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseError(String body) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(body, Map.class);
        } catch (Exception e) {
            return Map.of("codigo", "UNKNOWN", "mensaje", body);
        }
    }
}
