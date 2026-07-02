CREATE TABLE IF NOT EXISTS medico (
    id          UUID        PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    especialidad VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS paciente (
    id       UUID         PRIMARY KEY,
    nombre   VARCHAR(100) NOT NULL,
    telefono VARCHAR(20)  NOT NULL
);

CREATE TABLE IF NOT EXISTS disponibilidad_medico (
    id           UUID    PRIMARY KEY,
    medico_id    UUID    NOT NULL REFERENCES medico(id),
    fecha        DATE    NOT NULL,
    hora_inicio  TIME    NOT NULL,
    hora_fin     TIME    NOT NULL,
    estado       VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
    CONSTRAINT uq_disponibilidad UNIQUE (medico_id, fecha, hora_inicio)
);

CREATE TABLE IF NOT EXISTS cita (
    id               UUID         PRIMARY KEY,
    paciente_id      UUID         NOT NULL REFERENCES paciente(id),
    medico_id        UUID         NOT NULL REFERENCES medico(id),
    disponibilidad_id UUID        NOT NULL REFERENCES disponibilidad_medico(id),
    fecha            DATE         NOT NULL,
    hora_inicio      TIME         NOT NULL,
    hora_fin         TIME         NOT NULL,
    estado           VARCHAR(20)  NOT NULL DEFAULT 'CONFIRMADA',
    canal_creacion   VARCHAR(20)  NOT NULL DEFAULT 'ONLINE',
    creada_en        TIMESTAMP    NOT NULL,
    CONSTRAINT uq_cita_idempotente UNIQUE (paciente_id, disponibilidad_id)
);

CREATE TABLE IF NOT EXISTS notificacion_whatsapp (
    id             UUID        PRIMARY KEY,
    cita_id        UUID        NOT NULL REFERENCES cita(id),
    estado         VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    intentos       INT         NOT NULL DEFAULT 0,
    ultimo_intento TIMESTAMP,
    error          VARCHAR(500)
);
