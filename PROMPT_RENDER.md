# Prompt para publicar el sistema en Render

Usa este prompt con una IA que tenga acceso al repositorio y al panel de Render:

```text
Ayudame a publicar este monorepo en Render usando el archivo render.yaml existente.

Arquitectura:
- clinica-frontend: Angular 17 como Static Site.
- auth-service: Spring Boot 3, Java 21 y JWT RSA.
- apirest-demo: Spring Boot 4, Java 21 y API clinica.
- clinica-db: PostgreSQL administrado por Render.
- Ambos backends comparten la base, pero cada uno tiene su propia tabla de historial Flyway.

Antes de crear el Blueprint:
1. Genera un nuevo par RSA. La clave privada debe ser PKCS#8 y la publica X.509:
   openssl genpkey -algorithm RSA -out private.pem -pkeyopt rsa_keygen_bits:2048
   openssl pkey -in private.pem -pubout -out public.pem
2. Convierte cada archivo PEM completo a Base64 de una sola linea. No publiques ni guardes la clave privada en Git.
3. El repositorio ya no incluye las claves anteriores. Consideralas comprometidas y no las reutilices.

Crea un Blueprint desde render.yaml. Render solicitara las variables marcadas con sync: false. Usa estos valores:

Servicio clinica-auth:
- CORS_ALLOWED_ORIGINS: URL HTTPS final del frontend, normalmente https://clinica-frontend.onrender.com
- CLINICA_FRONTEND_URL: la misma URL del frontend
- RSA_PRIVATE_KEY_BASE64: Base64 del archivo private.pem completo
- RSA_PUBLIC_KEY_BASE64: Base64 del archivo public.pem completo
- INITIAL_ADMIN_USERNAME: nombre del administrador inicial
- INITIAL_ADMIN_EMAIL: correo del administrador inicial
- INITIAL_ADMIN_PASSWORD: contrasena robusta y unica

Servicio clinica-api:
- CORS_ALLOWED_ORIGINS: URL HTTPS final del frontend
- RSA_PUBLIC_KEY_BASE64: exactamente el mismo Base64 publico usado en clinica-auth

Sitio clinica-frontend:
- AUTH_API_BASE_URL: URL HTTPS final de clinica-auth, normalmente https://clinica-auth.onrender.com
- CLINIC_API_BASE_URL: URL HTTPS final de clinica-api, normalmente https://clinica-api.onrender.com

Si Render agrega sufijos a los nombres, actualiza las variables con las URLs reales y vuelve a desplegar los tres servicios. No uses localhost en ninguna variable de produccion.

Verificaciones obligatorias:
1. Confirma que /actuator/health devuelve UP en ambos backends.
2. Confirma que Flyway creo las tablas y que Hibernate valido el esquema.
3. Abre el frontend y prueba el login con el administrador inicial.
4. Comprueba que el JWT emitido por clinica-auth funciona en clinica-api.
5. Prueba navegacion directa a una ruta Angular para verificar el rewrite a index.html.
6. Prueba crear una especialidad, un medico, un paciente y una cita.
7. Revisa que Swagger no este disponible con el perfil prod.
8. No muestres secretos en capturas, logs ni respuestas.

Si un despliegue falla, analiza primero el log exacto de Render y propone el cambio minimo. No cambies PostgreSQL por otro motor, no vuelvas a agregar claves al repositorio y no reemplaces Flyway por ddl-auto=update.
```
