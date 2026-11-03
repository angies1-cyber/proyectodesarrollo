# Sistema de Gestión Clínica

Aplicación web para administrar pacientes, médicos, especialidades y citas. Incluye autenticación JWT, menú dinámico y permisos configurables por módulo.

## Componentes

| Aplicación | Puerto | Función |
|---|---:|---|
| `clinica-frontend` | 4200 | Interfaz Angular |
| `auth-service` | 8081 | Usuarios, roles, permisos y JWT |
| `apirest-demo` | 8082 | API de gestión clínica |
| SQL Server | 1435 | Base de datos `proyecto_clientes` |

## Requisitos

- Java 21
- Node.js y npm
- Docker
- SQL Server disponible en el puerto 1435

## Inicio rápido

1. Crea `.env` a partir de `.env.example`.
2. Inicia SQL Server.
3. Si vienes del esquema de seguridad anterior, ejecuta `auth-service/reset-security-schema.sql` una sola vez.
4. Inicia cada aplicación:

```powershell
# Terminal 1
cd auth-service
.\mvnw.cmd spring-boot:run

# Terminal 2
cd apirest-demo
.\mvnw.cmd spring-boot:run

# Terminal 3
cd clinica-frontend
npm install
npm start
```

Abre `http://localhost:4200`.

También puedes construir los servicios con `docker compose up --build`.

## Usuario inicial

La cuenta de superadministración se crea únicamente cuando se configuran
`INITIAL_ADMIN_USERNAME`, `INITIAL_ADMIN_EMAIL` e `INITIAL_ADMIN_PASSWORD`.


## Seguridad

El modelo utiliza estas relaciones:

```text
usuarios <-> usuario_roles <-> roles -> sistemas
roles <-> roles_modulos <-> modulos
roles <-> roles_modulos_permisos <-> modulos + permisos
modulos -> modulo_padre
```

Los permisos disponibles son `READ`, `CREATE`, `UPDATE`, `DELETE` y `RESET_PASSWORD`. Las autoridades del JWT incluyen el módulo, por ejemplo `USUARIOS:CREATE`.

Crear usuarios, eliminarlos y restablecer sus contraseñas requieren permisos independientes. El último superadministrador habilitado no se puede eliminar, deshabilitar ni degradar.

## Verificación

```powershell
cd auth-service
.\mvnw.cmd test

cd ..\apirest-demo
.\mvnw.cmd test

cd ..\clinica-frontend
npm run build
```

La documentación ampliada se encuentra en `Guia_completa_proyecto_clinica.docx`.
