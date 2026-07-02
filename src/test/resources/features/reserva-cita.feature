# language: es
Característica: Reserva de Cita en Línea 24/7
  Como paciente
  Quiero reservar una cita en línea en cualquier momento del día
  Para no tener que llamar durante mi horario de almuerzo

  Escenario: Reserva exitosa fuera del horario telefónico
    Dado que el paciente con id "b1b2c3d4-0001-0001-0001-000000000001" existe en el sistema
    Y la franja horaria "c1000001-0001-0001-0001-000000000001" está disponible
    Cuando el paciente confirma la reserva de la franja "c1000001-0001-0001-0001-000000000001"
    Entonces la cita queda registrada con estado "CONFIRMADA"
    Y el sistema emite una notificación WhatsApp al paciente

  Escenario: Verificación de cita en historial del paciente
    Dado que el paciente con id "b1b2c3d4-0001-0001-0001-000000000001" tiene una cita confirmada
    Cuando consulta su historial de citas
    Entonces la cita aparece con estado "CONFIRMADA" y los datos completos del médico

  @us2
  Escenario: Intento de reserva en franja no disponible
    Dado que la franja horaria "e1us2test-0001-0001-0001-000000000099" está ocupada
    Cuando el paciente con id "b1b2c3d4-0001-0001-0001-000000000001" intenta confirmarla
    Entonces el sistema responde con código de error "FRANJA_NO_DISPONIBLE"
    Y el mensaje invita al paciente a elegir otra franja

  @us2
  Escenario: Reserva alternativa tras rechazo de franja ocupada
    Dado que la franja horaria "e1us2test-0001-0001-0001-000000000099" está ocupada
    Y la franja horaria "c1000001-0001-0001-0001-000000000003" está disponible
    Cuando el paciente con id "b1b2c3d4-0001-0001-0001-000000000001" confirma la franja "c1000001-0001-0001-0001-000000000003"
    Entonces la cita queda registrada con estado "CONFIRMADA"
