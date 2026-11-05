INSERT INTO especialidades (nombre, descripcion, activo)
VALUES
    ('Medicina Interna', 'Diagnostico y tratamiento integral de enfermedades en adultos.', TRUE),
    ('Pediatria', 'Atencion medica preventiva y curativa para ninos y adolescentes.', TRUE),
    ('Cardiologia', 'Prevencion, diagnostico y tratamiento de enfermedades cardiovasculares.', TRUE),
    ('Dermatologia', 'Atencion de enfermedades de la piel, cabello y unas.', TRUE),
    ('Ginecologia', 'Salud integral del sistema reproductor femenino.', TRUE),
    ('Traumatologia', 'Diagnostico y tratamiento de lesiones del sistema musculoesqueletico.', TRUE),
    ('Neurologia', 'Atencion de trastornos del cerebro y sistema nervioso.', TRUE),
    ('Oftalmologia', 'Evaluacion y tratamiento de enfermedades de los ojos.', TRUE),
    ('Otorrinolaringologia', 'Atencion de oido, nariz y garganta.', TRUE),
    ('Endocrinologia', 'Diagnostico y control de trastornos hormonales y metabolicos.', TRUE),
    ('Gastroenterologia', 'Atencion de enfermedades del aparato digestivo.', TRUE),
    ('Neumologia', 'Diagnostico y tratamiento de enfermedades respiratorias.', TRUE),
    ('Psiquiatria', 'Evaluacion y tratamiento de trastornos de salud mental.', TRUE),
    ('Urologia', 'Atencion del sistema urinario y reproductor masculino.', TRUE),
    ('Medicina Familiar', 'Atencion primaria continua para pacientes de todas las edades.', TRUE)
ON CONFLICT (nombre) DO UPDATE
SET activo = EXCLUDED.activo;

INSERT INTO enfermedades (nombre, descripcion, activo)
SELECT seed.nombre, seed.descripcion, seed.activo
FROM (VALUES
    ('Hipertension arterial', 'Elevacion persistente de la presion arterial.', TRUE),
    ('Diabetes mellitus tipo 2', 'Alteracion cronica del metabolismo de la glucosa.', TRUE),
    ('Asma bronquial', 'Inflamacion cronica de las vias respiratorias.', TRUE),
    ('Gastritis', 'Inflamacion de la mucosa del estomago.', TRUE),
    ('Migrana', 'Cefalea recurrente de intensidad moderada o severa.', TRUE),
    ('Dermatitis atopica', 'Afeccion inflamatoria cronica de la piel.', TRUE),
    ('Artritis reumatoide', 'Enfermedad autoinmune que afecta principalmente las articulaciones.', TRUE),
    ('Hipotiroidismo', 'Produccion insuficiente de hormonas tiroideas.', TRUE),
    ('Rinitis alergica', 'Inflamacion nasal provocada por alergenos.', TRUE),
    ('Neumonia adquirida en la comunidad', 'Infeccion pulmonar contraida fuera del entorno hospitalario.', TRUE),
    ('Reflujo gastroesofagico', 'Retorno recurrente del contenido gastrico al esofago.', TRUE),
    ('Infeccion urinaria', 'Infeccion bacteriana de alguna parte del tracto urinario.', TRUE),
    ('Anemia ferropenica', 'Disminucion de hemoglobina asociada a deficiencia de hierro.', TRUE),
    ('Otitis media', 'Inflamacion o infeccion del oido medio.', TRUE),
    ('Conjuntivitis', 'Inflamacion de la membrana que recubre el ojo y los parpados.', TRUE)
) AS seed(nombre, descripcion, activo)
WHERE NOT EXISTS (
    SELECT 1
    FROM enfermedades existente
    WHERE LOWER(existente.nombre) = LOWER(seed.nombre)
);

INSERT INTO pacientes (
    nombre, apellido, dpi, telefono, correo, fecha_nacimiento, activo,
    direccion, fecha_creacion, fecha_inhabilitacion, telefono_alterno
)
VALUES
    ('Ana Lucia', 'Garcia Lopez', '1001001000101', '41010001', 'ana.garcia@example.com', DATE '1988-03-14', TRUE, 'Zona 1, Ciudad de Guatemala', DATE '2024-01-08', NULL, '51010001'),
    ('Carlos Eduardo', 'Martinez Perez', '1001001000102', '41010002', 'carlos.martinez@example.com', DATE '1976-11-02', TRUE, 'Zona 7, Ciudad de Guatemala', DATE '2024-01-09', NULL, '51010002'),
    ('Maria Fernanda', 'Rodriguez Diaz', '1001001000103', '41010003', 'maria.rodriguez@example.com', DATE '1995-07-21', TRUE, 'Mixco, Guatemala', DATE '2024-01-10', NULL, '51010003'),
    ('Jose Andres', 'Hernandez Ruiz', '1001001000104', '41010004', 'jose.hernandez@example.com', DATE '1982-01-30', TRUE, 'Villa Nueva, Guatemala', DATE '2024-01-11', NULL, NULL),
    ('Sofia Isabel', 'Morales Castro', '1001001000105', '41010005', 'sofia.morales@example.com', DATE '2001-09-12', TRUE, 'San Miguel Petapa, Guatemala', DATE '2024-01-12', NULL, '51010005'),
    ('Luis Fernando', 'Cabrera Mendez', '1001001000106', '41010006', 'luis.cabrera@example.com', DATE '1969-05-18', TRUE, 'Amatitlan, Guatemala', DATE '2024-01-15', NULL, '51010006'),
    ('Gabriela Alejandra', 'Vasquez Soto', '1001001000107', '41010007', 'gabriela.vasquez@example.com', DATE '1992-12-06', TRUE, 'Zona 12, Ciudad de Guatemala', DATE '2024-01-16', NULL, NULL),
    ('Miguel Angel', 'Ramirez Ortiz', '1001001000108', '41010008', 'miguel.ramirez@example.com', DATE '1985-04-25', TRUE, 'Santa Catarina Pinula, Guatemala', DATE '2024-01-17', NULL, '51010008'),
    ('Daniela Andrea', 'Castillo Reyes', '1001001000109', '41010009', 'daniela.castillo@example.com', DATE '1998-08-09', TRUE, 'Zona 15, Ciudad de Guatemala', DATE '2024-01-18', NULL, '51010009'),
    ('Jorge Mario', 'Aguilar Fuentes', '1001001000110', '41010010', 'jorge.aguilar@example.com', DATE '1973-02-17', TRUE, 'Fraijanes, Guatemala', DATE '2024-01-19', NULL, NULL),
    ('Paola Beatriz', 'Mendoza Leon', '1001001000111', '41010011', 'paola.mendoza@example.com', DATE '1990-10-28', TRUE, 'Zona 10, Ciudad de Guatemala', DATE '2024-01-22', NULL, '51010011'),
    ('Ricardo Antonio', 'Gomez Alvarado', '1001001000112', '41010012', 'ricardo.gomez@example.com', DATE '1980-06-11', TRUE, 'San Jose Pinula, Guatemala', DATE '2024-01-23', NULL, '51010012'),
    ('Valeria Nicole', 'Perez Salazar', '1001001000113', '41010013', 'valeria.perez@example.com', DATE '2003-03-03', TRUE, 'Zona 6, Ciudad de Guatemala', DATE '2024-01-24', NULL, NULL),
    ('Oscar Estuardo', 'Lopez Chacon', '1001001000114', '41010014', 'oscar.lopez@example.com', DATE '1965-12-19', TRUE, 'Chinautla, Guatemala', DATE '2024-01-25', NULL, '51010014'),
    ('Elena Patricia', 'Santos Ibarra', '1001001000115', '41010015', 'elena.santos@example.com', DATE '1987-07-07', TRUE, 'Zona 16, Ciudad de Guatemala', DATE '2024-01-26', NULL, '51010015')
ON CONFLICT (dpi) DO UPDATE
SET activo = EXCLUDED.activo;

INSERT INTO medicos (
    nombre, apellido, dpi, fecha_nacimiento, telefono, correo, direccion,
    activo, fecha_creacion, especialidad_id
)
VALUES
    ('Alejandro', 'Pineda Morales', '2002002000201', DATE '1978-02-12', '42020001', 'alejandro.pineda@example.com', 'Zona 9, Ciudad de Guatemala', TRUE, DATE '2024-01-03', (SELECT id FROM especialidades WHERE nombre = 'Medicina Interna')),
    ('Patricia', 'Juarez Molina', '2002002000202', DATE '1984-06-24', '42020002', 'patricia.juarez@example.com', 'Zona 14, Ciudad de Guatemala', TRUE, DATE '2024-01-03', (SELECT id FROM especialidades WHERE nombre = 'Pediatria')),
    ('Roberto', 'Alvarez Soto', '2002002000203', DATE '1975-09-08', '42020003', 'roberto.alvarez@example.com', 'Zona 10, Ciudad de Guatemala', TRUE, DATE '2024-01-03', (SELECT id FROM especialidades WHERE nombre = 'Cardiologia')),
    ('Claudia', 'Monterroso Paz', '2002002000204', DATE '1987-01-16', '42020004', 'claudia.monterroso@example.com', 'Mixco, Guatemala', TRUE, DATE '2024-01-04', (SELECT id FROM especialidades WHERE nombre = 'Dermatologia')),
    ('Fernando', 'Herrera Caal', '2002002000205', DATE '1981-11-03', '42020005', 'fernando.herrera@example.com', 'Zona 15, Ciudad de Guatemala', TRUE, DATE '2024-01-04', (SELECT id FROM especialidades WHERE nombre = 'Ginecologia')),
    ('Monica', 'Estrada Giron', '2002002000206', DATE '1979-04-27', '42020006', 'monica.estrada@example.com', 'Villa Nueva, Guatemala', TRUE, DATE '2024-01-04', (SELECT id FROM especialidades WHERE nombre = 'Traumatologia')),
    ('Sergio', 'Velasquez Rios', '2002002000207', DATE '1983-08-19', '42020007', 'sergio.velasquez@example.com', 'Zona 2, Ciudad de Guatemala', TRUE, DATE '2024-01-05', (SELECT id FROM especialidades WHERE nombre = 'Neurologia')),
    ('Lucia', 'Figueroa Barrios', '2002002000208', DATE '1988-12-05', '42020008', 'lucia.figueroa@example.com', 'Santa Catarina Pinula, Guatemala', TRUE, DATE '2024-01-05', (SELECT id FROM especialidades WHERE nombre = 'Oftalmologia')),
    ('Hector', 'Cifuentes Cano', '2002002000209', DATE '1976-03-22', '42020009', 'hector.cifuentes@example.com', 'Zona 11, Ciudad de Guatemala', TRUE, DATE '2024-01-05', (SELECT id FROM especialidades WHERE nombre = 'Otorrinolaringologia')),
    ('Adriana', 'Solorzano Meza', '2002002000210', DATE '1985-07-14', '42020010', 'adriana.solorzano@example.com', 'Fraijanes, Guatemala', TRUE, DATE '2024-01-06', (SELECT id FROM especialidades WHERE nombre = 'Endocrinologia')),
    ('Mauricio', 'Ordonez Lara', '2002002000211', DATE '1980-10-09', '42020011', 'mauricio.ordonez@example.com', 'Zona 13, Ciudad de Guatemala', TRUE, DATE '2024-01-06', (SELECT id FROM especialidades WHERE nombre = 'Gastroenterologia')),
    ('Veronica', 'Samayoa Pinto', '2002002000212', DATE '1986-05-31', '42020012', 'veronica.samayoa@example.com', 'San Jose Pinula, Guatemala', TRUE, DATE '2024-01-06', (SELECT id FROM especialidades WHERE nombre = 'Neumologia')),
    ('Esteban', 'Rosales Arriaga', '2002002000213', DATE '1977-01-25', '42020013', 'esteban.rosales@example.com', 'Zona 4, Ciudad de Guatemala', TRUE, DATE '2024-01-07', (SELECT id FROM especialidades WHERE nombre = 'Psiquiatria')),
    ('Natalia', 'Galindo Quezada', '2002002000214', DATE '1989-09-17', '42020014', 'natalia.galindo@example.com', 'Zona 16, Ciudad de Guatemala', TRUE, DATE '2024-01-07', (SELECT id FROM especialidades WHERE nombre = 'Urologia')),
    ('Diego', 'Recinos Lemus', '2002002000215', DATE '1982-06-06', '42020015', 'diego.recinos@example.com', 'Amatitlan, Guatemala', TRUE, DATE '2024-01-07', (SELECT id FROM especialidades WHERE nombre = 'Medicina Familiar'))
ON CONFLICT (dpi) DO UPDATE
SET activo = EXCLUDED.activo;

INSERT INTO citas (
    paciente_id, medico_id, fecha, hora, motivo, estado,
    fecha_creacion, fecha_modificacion
)
SELECT
    paciente.id,
    medico.id,
    seed.fecha,
    seed.hora,
    seed.motivo,
    seed.estado,
    seed.fecha_creacion,
    seed.fecha_modificacion
FROM (VALUES
    ('1001001000101', '2002002000201', DATE '2025-02-03', TIME '08:00', 'Evaluacion general y control preventivo', 'COMPLETADA', DATE '2025-01-20', DATE '2025-02-03'),
    ('1001001000102', '2002002000203', DATE '2025-02-05', TIME '09:00', 'Control de presion arterial', 'COMPLETADA', DATE '2025-01-21', DATE '2025-02-05'),
    ('1001001000103', '2002002000204', DATE '2025-02-07', TIME '10:30', 'Evaluacion de irritacion cutanea', 'COMPLETADA', DATE '2025-01-23', DATE '2025-02-07'),
    ('1001001000104', '2002002000211', DATE '2025-02-10', TIME '11:00', 'Consulta por acidez recurrente', 'COMPLETADA', DATE '2025-01-27', DATE '2025-02-10'),
    ('1001001000105', '2002002000208', DATE '2025-02-12', TIME '14:00', 'Revision de agudeza visual', 'COMPLETADA', DATE '2025-01-29', DATE '2025-02-12'),
    ('1001001000106', '2002002000206', DATE '2025-02-14', TIME '15:30', 'Dolor persistente en rodilla derecha', 'COMPLETADA', DATE '2025-01-30', DATE '2025-02-14'),
    ('1001001000107', '2002002000205', DATE '2025-02-17', TIME '08:30', 'Control ginecologico anual', 'COMPLETADA', DATE '2025-02-03', DATE '2025-02-17'),
    ('1001001000108', '2002002000207', DATE '2025-02-19', TIME '09:30', 'Evaluacion por cefaleas recurrentes', 'COMPLETADA', DATE '2025-02-04', DATE '2025-02-19'),
    ('1001001000109', '2002002000209', DATE '2025-02-21', TIME '13:00', 'Molestia de oido y congestion nasal', 'COMPLETADA', DATE '2025-02-06', DATE '2025-02-21'),
    ('1001001000110', '2002002000210', DATE '2025-02-24', TIME '16:00', 'Seguimiento de funcion tiroidea', 'COMPLETADA', DATE '2025-02-10', DATE '2025-02-24'),
    ('1001001000111', '2002002000212', DATE '2025-02-26', TIME '10:00', 'Evaluacion por tos persistente', 'COMPLETADA', DATE '2025-02-11', DATE '2025-02-26'),
    ('1001001000112', '2002002000213', DATE '2025-02-28', TIME '11:30', 'Consulta por ansiedad e insomnio', 'COMPLETADA', DATE '2025-02-13', DATE '2025-02-28'),
    ('1001001000113', '2002002000202', DATE '2025-03-03', TIME '14:30', 'Evaluacion integral de adolescente', 'COMPLETADA', DATE '2025-02-17', DATE '2025-03-03'),
    ('1001001000114', '2002002000214', DATE '2025-03-05', TIME '08:00', 'Control por sintomas urinarios', 'COMPLETADA', DATE '2025-02-19', DATE '2025-03-05'),
    ('1001001000115', '2002002000215', DATE '2025-03-07', TIME '09:00', 'Chequeo anual de salud', 'CANCELADA', DATE '2025-02-21', DATE '2025-03-05')
) AS seed(
    paciente_dpi, medico_dpi, fecha, hora, motivo, estado,
    fecha_creacion, fecha_modificacion
)
JOIN pacientes paciente
    ON paciente.dpi = seed.paciente_dpi
   AND paciente.activo = TRUE
JOIN medicos medico
    ON medico.dpi = seed.medico_dpi
   AND medico.activo = TRUE
WHERE NOT EXISTS (
    SELECT 1
    FROM citas existente
    WHERE existente.paciente_id = paciente.id
      AND existente.medico_id = medico.id
      AND existente.fecha = seed.fecha
      AND existente.hora = seed.hora
);
