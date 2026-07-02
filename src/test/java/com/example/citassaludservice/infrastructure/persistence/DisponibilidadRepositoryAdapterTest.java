package com.example.citassaludservice.infrastructure.persistence;

import com.example.citassaludservice.domain.model.DisponibilidadMedico;
import com.example.citassaludservice.domain.model.EstadoDisponibilidad;
import com.example.citassaludservice.infrastructure.persistence.adapter.DisponibilidadRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Sql(scripts = {"/db/test-data-us1.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
class DisponibilidadRepositoryAdapterTest {

    @Autowired
    DisponibilidadRepositoryAdapter adapter;

    private static final UUID DISPONIBLE_ID =
            UUID.fromString("d1000001-0001-0001-0001-000000000001");

    @Test
    void given_disponible_franja_when_findByIdForUpdate_then_returns_disponible() {
        Optional<DisponibilidadMedico> result = adapter.findByIdForUpdate(DISPONIBLE_ID);
        assertThat(result).isPresent();
        assertThat(result.get().getEstado()).isEqualTo(EstadoDisponibilidad.DISPONIBLE);
    }

    @Test
    void given_nonexistent_id_when_findByIdForUpdate_then_returns_empty() {
        Optional<DisponibilidadMedico> result = adapter.findByIdForUpdate(UUID.randomUUID());
        assertThat(result).isEmpty();
    }

    @Test
    void given_disponible_when_save_after_reservar_then_estado_persisted_as_ocupada() {
        DisponibilidadMedico d = adapter.findByIdForUpdate(DISPONIBLE_ID).orElseThrow();
        d.reservar();
        adapter.save(d);

        DisponibilidadMedico reloaded = adapter.findByIdForUpdate(DISPONIBLE_ID).orElseThrow();
        assertThat(reloaded.getEstado()).isEqualTo(EstadoDisponibilidad.OCUPADA);
    }
}
