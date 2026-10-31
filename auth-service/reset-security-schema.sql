-- Ejecutar una sola vez al migrar desde el modelo anterior.
-- No elimina pacientes, médicos, especialidades, enfermedades ni citas.
IF OBJECT_ID('dbo.roles_modulos_permisos', 'U') IS NOT NULL DROP TABLE dbo.roles_modulos_permisos;
IF OBJECT_ID('dbo.roles_modulos', 'U') IS NOT NULL DROP TABLE dbo.roles_modulos;
IF OBJECT_ID('dbo.usuario_roles', 'U') IS NOT NULL DROP TABLE dbo.usuario_roles;
IF OBJECT_ID('dbo.rol_permisos', 'U') IS NOT NULL DROP TABLE dbo.rol_permisos;
IF OBJECT_ID('dbo.usuarios', 'U') IS NOT NULL DROP TABLE dbo.usuarios;
IF OBJECT_ID('dbo.roles', 'U') IS NOT NULL DROP TABLE dbo.roles;
IF OBJECT_ID('dbo.modulos', 'U') IS NOT NULL DROP TABLE dbo.modulos;
IF OBJECT_ID('dbo.permisos', 'U') IS NOT NULL DROP TABLE dbo.permisos;
IF OBJECT_ID('dbo.sistemas', 'U') IS NOT NULL DROP TABLE dbo.sistemas;
