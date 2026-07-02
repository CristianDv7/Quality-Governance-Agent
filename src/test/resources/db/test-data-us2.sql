-- US2 fixture: one slot in OCUPADA state for rejection scenario tests
MERGE INTO disponibilidad_medico (id, medico_id, fecha, hora_inicio, hora_fin, estado)
    VALUES ('e1000002-0001-0001-0001-000000000099', 'a1b2c3d4-0001-0001-0001-000000000001', DATEADD('DAY', 7, CURRENT_DATE), '14:00', '14:30', 'OCUPADA');
