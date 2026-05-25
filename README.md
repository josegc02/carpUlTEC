# Carpool UTEC - Backend API

Backend universitario para coordinar viajes compartidos entre estudiantes de UTEC. El proyecto permite que un usuario publique un viaje como conductor o solicite movilidad como pasajero, manteniendo reglas simples de propiedad, cupos, seguridad y reputacion.

## Estado Del Proyecto

| Bloque | Estado | Evidencia o pendiente |
| --- | --- | --- |
| Entidades, relaciones JPA y DTOs | Implementado | Modelos para usuarios, vehiculos, publicaciones, solicitudes, viajes, pasajeros y reviews. |
| Autenticacion y seguridad JWT | Implementado | Registro/login, access token, refresh token, roles `USER` y `ADMIN`. |
| Logica principal de carpool | Implementado | Vehiculo del conductor, validacion de cupos, aceptacion que crea el viaje y ownership basico. |
| Reviews y rating | Implementado | Reviewer autenticado, participantes reales y rating calculado desde reviews. |
| Google Maps | Implementado/configurable | Usa `GOOGLE_MAPS_API_KEY`; la clave no se guarda en Git. |
| Tests | Implementado | `200` tests ejecutados correctamente con `.\mvnw.cmd test`, incluyendo PostgreSQL con Testcontainers/Docker. |
| Coleccion Postman | Incluida | Archivo `postman_collection.json`. |
| AWS Deployment | **[PENDIENTE]** | Agregar infraestructura, URL publica y evidencias una vez desplegado. |

## Problema Y Solucion

Los estudiantes pueden necesitar compartir el traslado hacia o desde UTEC, pero coordinar por mensajes sueltos dificulta saber quien conduce, cuantos cupos hay y si la persona realmente participo en un viaje anterior. Carpool UTEC organiza ese flujo en una API REST.

Un usuario registrado puede actuar como conductor en un viaje y como pasajero en otro. No existen roles permanentes `DRIVER` o `PASSENGER`; los unicos roles del sistema son `USER` y `ADMIN`. El rol de viaje se define por la publicacion o la solicitud:

- Una publicacion de conductor ofrece cupos y debe seleccionar un vehiculo propio.
- Una publicacion de pasajero indica los cupos que necesita.
- La solicitud siempre debe ser del rol opuesto a la publicacion.
- El viaje se crea al aceptar una solicitud; no se crea manualmente desde un endpoint publico.

## Tecnologias

| Tecnologia | Uso |
| --- | --- |
| Java 17 | Lenguaje configurado en Maven. |
| Spring Boot 3.4.5 | Aplicacion web y configuracion principal. |
| Spring Web | API REST. |
| Spring Data JPA | Persistencia y relaciones. |
| PostgreSQL | Base de datos relacional. |
| Spring Security + JJWT | Autenticacion stateless con JWT. |
| Jakarta Validation | Validacion de requests. |
| Testcontainers | PostgreSQL real para pruebas de repositorio e integracion. |
| JavaMailSender | Correo para eventos de registro/cambio de solicitud, cuando se configura SMTP. |
| Google Maps API | Geocodificacion y distancia configurable. |

## Arquitectura

Se conserva una estructura directa y apropiada para un proyecto de curso:

```text
controller -> service -> repository -> model
                  |
                 dto / exception / event / listener / security
```

- `controller`: expone endpoints y obtiene el usuario autenticado.
- `service`: aplica reglas de negocio y conversion a DTOs.
- `repository`: acceso a PostgreSQL con JPA.
- `model`: entidades y relaciones.
- `security`: filtro JWT, roles y configuracion CORS.
- `event` y `listener`: eventos sencillos para notificaciones asincronas.

No se usan microservicios ni patrones complejos; la prioridad es que el flujo de carpool sea legible y validable.

## Entidades Principales

| Entidad | Responsabilidad |
| --- | --- |
| `User` | Estudiante registrado, rol de sistema y rating calculado. |
| `Vehicle` | Vehiculo perteneciente al usuario autenticado. |
| `Publication` | Oferta de conductor o necesidad de pasajero. |
| `RequestPublication` | Solicitud/respuesta a una publicacion con estado `PENDING`, `ACCEPTED`, `REJECTED` o `CANCELLED`. |
| `Ride` | Viaje confirmado creado desde la aceptacion. |
| `RidePassenger` | Pasajero confirmado y cupos reservados. |
| `Review` | Calificacion entre participantes de un viaje realizado. |

## Reglas De Negocio Implementadas

### Registro Y Seguridad

- Solo se permite registrar correos que terminen en `@utec.edu.pe`.
- La contrasena requiere al menos ocho caracteres, letras y numeros.
- Las contrasenas se almacenan codificadas con BCrypt.
- El login devuelve `accessToken` y `refreshToken`.
- El token identifica al usuario que crea o modifica recursos propios.
- `USER` y `ADMIN` son roles del sistema; conducir o viajar depende de cada viaje.

### Vehiculos Y Publicaciones

- El propietario del vehiculo se obtiene del JWT, no del body enviado por el cliente.
- Un usuario puede registrar hasta dos vehiculos.
- Para publicar como conductor (`driverToPassenger = true`) debe enviarse `vehicleId`.
- El vehiculo seleccionado debe pertenecer al autor autenticado.
- Los cupos ofrecidos no pueden exceder los asientos del vehiculo.
- Una publicacion como pasajero no puede asociar un vehiculo.

### Solicitudes Y Viajes

- Nadie puede responder a su propia publicacion.
- Una publicacion de conductor recibe solicitudes de pasajeros; una publicacion de pasajero recibe respuestas de conductores.
- Quien responde como conductor debe tener un vehiculo registrado.
- Una solicitud solo puede modificarse mientras esta `PENDING`.
- Al editar una solicitud no se permite cambiar la publicacion ni el rol de la solicitud.
- Solo el requester puede cancelar su solicitud.
- Solo el autor de la publicacion puede aceptar o rechazar.
- En publicaciones de conductor se controlan los cupos ya reservados antes de aceptar otro pasajero.
- En publicaciones de pasajero, el conductor debe ofrecer y tener suficientes asientos.
- `Ride` y `RidePassenger` se crean desde `accept()`; `POST /api/rides` y `POST /api/ride-passengers` no forman parte del flujo permitido.

### Reviews Y Rating

- El autor de una review sale del usuario autenticado; no se acepta `reviewerId` del body.
- Un usuario no puede calificarse a si mismo.
- Solo se puede calificar despues de la hora del viaje.
- Reviewer y reviewed deben haber participado en el viaje.
- No se permite repetir la misma review para el mismo viaje y participantes.
- El rating de un usuario se calcula desde las reviews recibidas; no se acepta desde requests de usuario.

## API Principal

`Authorization: Bearer <accessToken>` es requerido salvo donde se indica como publico.

| Metodo | Ruta | Acceso | Descripcion |
| --- | --- | --- | --- |
| `POST` | `/api/auth/register` | Publico | Registrar usuario UTEC. |
| `POST` | `/api/auth/login` | Publico | Iniciar sesion y recibir tokens. |
| `POST` | `/api/auth/refresh` | Publico | Renovar tokens. |
| `GET` | `/api/users/me` | JWT | Ver usuario autenticado. |
| `GET` | `/api/users` | ADMIN | Listar usuarios. |
| `POST` | `/api/vehicles` | JWT | Registrar vehiculo propio. |
| `PUT/DELETE` | `/api/vehicles/{id}` | JWT/owner | Administrar vehiculo propio. |
| `GET` | `/api/publications` | Publico | Listar publicaciones. |
| `GET` | `/api/publications/{id}` | Publico | Ver publicacion. |
| `POST` | `/api/publications` | JWT | Crear publicacion. |
| `PUT/DELETE` | `/api/publications/{id}` | JWT/autor | Editar o eliminar publicacion propia. |
| `POST` | `/api/publications/{id}/requests` | JWT | Enviar solicitud a una publicacion. |
| `GET` | `/api/publications/{id}/requests` | JWT/autor | Revisar solicitudes recibidas. |
| `PATCH` | `/api/request-publications/{id}/accept` | JWT/autor | Aceptar solicitud y crear viaje. |
| `PATCH` | `/api/request-publications/{id}/reject` | JWT/autor | Rechazar solicitud. |
| `PATCH` | `/api/request-publications/{id}/cancel` | JWT/requester | Cancelar solicitud. |
| `GET` | `/api/rides` | JWT | Consultar viajes. |
| `GET` | `/api/ride-passengers` | JWT | Consultar pasajeros confirmados. |
| `POST` | `/api/reviews` | JWT | Calificar participante despues del viaje. |
| `GET` | `/api/reviews` | JWT | Consultar reviews. |

## Configuracion Local

### Requisitos

- Java 17 o superior.
- Docker Desktop activo para ejecutar los tests con PostgreSQL Testcontainers.
- PostgreSQL disponible si se desea iniciar la aplicacion local fuera de pruebas.

### Variables De Entorno

No se deben commitear claves ni passwords. Para desarrollo se pueden definir variables en PowerShell antes de iniciar la aplicacion:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/carpool"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="TU_PASSWORD_LOCAL"
$env:SPRING_JPA_HIBERNATE_DDL_AUTO="update"
$env:APP_JWT_SECRET="TU_SECRET_BASE64_SEGURO"
$env:GOOGLE_MAPS_API_KEY="TU_GOOGLE_MAPS_API_KEY"
$env:CORS_ORIGINS="http://localhost:3000,http://localhost:5173"
.\mvnw.cmd spring-boot:run
```

| Variable | Uso | Obligatoria |
| --- | --- | --- |
| `SPRING_DATASOURCE_URL` | Conexion PostgreSQL para ejecutar la app. | Si se inicia localmente o en produccion. |
| `SPRING_DATASOURCE_USERNAME` | Usuario de base de datos. | Si se inicia la app. |
| `SPRING_DATASOURCE_PASSWORD` | Password de base de datos. | Si se inicia la app. |
| `APP_JWT_SECRET` | Firma de tokens JWT en Base64. | Requerida en produccion. |
| `GOOGLE_MAPS_API_KEY` | Geocodificacion/distancias. | Requerida para Google Maps real. |
| `CORS_ORIGINS` | Origenes permitidos para frontend. | Requerida al desplegar frontend. |
| `APP_MAIL_FROM` | Remitente de emails. | Opcional, junto con SMTP. |

## Google Maps

La API utiliza Google Maps de manera configurable:

- Si se proporciona `GOOGLE_MAPS_API_KEY`, las direcciones de publicaciones y solicitudes pueden convertirse en coordenadas y calcular distancia hacia UTEC.
- Si no hay clave configurada, la aplicacion sigue funcionando sin exponer secretos.
- La clave debe almacenarse como variable de entorno local o secreto de despliegue.

**[PENDIENTE PARA ENTREGA]** Agregar evidencia de una llamada exitosa a Google Maps o una captura de Postman sin mostrar la clave.

## Testing

Los tests usan PostgreSQL real dentro de Docker mediante Testcontainers. Con Docker Desktop abierto:

```powershell
.\mvnw.cmd test
```

Ultima validacion local realizada en esta rama:

```text
Tests run: 200, Failures: 0, Errors: 0, Skipped: 0
```

La suite cubre repositorios, servicios, controladores, JWT/security, reglas de vehiculos/publicaciones, aceptacion de solicitudes, reviews y configuracion de Google Maps.

## Postman

El archivo [`postman_collection.json`](./postman_collection.json) contiene solicitudes preparadas para validar el flujo principal:

1. Registrar e iniciar sesion como conductor.
2. Registrar e iniciar sesion como pasajero.
3. Crear vehiculo del conductor.
4. Crear publicacion del conductor usando su vehiculo.
5. Crear solicitud como pasajero.
6. Aceptar la solicitud como conductor.
7. Consultar el viaje creado.
8. Crear reviews cuando el viaje ya haya ocurrido.
9. Verificar que endpoints sensibles sin JWT respondan `401` y que no se permita crear rides manualmente.

La coleccion guarda automaticamente tokens e identificadores (`driverToken`, `passengerToken`, `vehicleId`, `publicationId`, `requestId` y `rideId`). Para probar en AWS, solo sera necesario reemplazar la variable `baseUrl`.

Para ejecutar el recorrido completo varias veces, use una base de datos limpia o actualice las variables con recursos existentes. Esto es esperado: el backend impide solicitudes duplicadas activas y limita la cantidad de vehiculos por usuario.

## Deployment AWS

**Estado: [PENDIENTE DE IMPLEMENTAR Y DOCUMENTAR]**

Segun el requerimiento del curso, el deployment final debera realizarse en AWS. Esta seccion debe completarse despues de tener infraestructura real y una URL verificable.

| Elemento AWS | Valor final |
| --- | --- |
| Region | `us-east-1` |
| ECR Repository | **[PENDIENTE]** |
| ECS Cluster / Service | **[PENDIENTE]** |
| RDS PostgreSQL endpoint privado | **[PENDIENTE - no publicar password]** |
| Application Load Balancer DNS | **[PENDIENTE]** |
| URL publica del backend | **[PENDIENTE]** |
| Health endpoint | **[PENDIENTE: implementar y verificar `/actuator/health`]** |

### Evidencias AWS Por Agregar

- **[PENDIENTE]** Captura de imagen subida a ECR.
- **[PENDIENTE]** Captura de tarea ECS en estado `RUNNING`.
- **[PENDIENTE]** Captura de target healthy en el Load Balancer.
- **[PENDIENTE]** Captura de RDS configurado sin mostrar credenciales.
- **[PENDIENTE]** Captura de Postman consumiendo la URL publica.
- **[PENDIENTE]** Instrucciones para apagar o eliminar recursos al finalizar la evaluacion.

## Equipo Y Evidencias Academicas

| Campo | Informacion |
| --- | --- |
| Curso | Desarrollo Basado en Plataformas - CS 2031 |
| Proyecto | Carpool UTEC |
| Integrantes | **[PENDIENTE: agregar nombres y codigos]** |
| Docente / Seccion | **[PENDIENTE]** |
| Repositorio GitHub | **[PENDIENTE: colocar URL de la rama entregada]** |
| Video o demo | **[PENDIENTE, si la entrega lo solicita]** |
| Diagrama ER / arquitectura | **[PENDIENTE: insertar enlace o imagen]** |

## Trabajo Pendiente Antes De Entrega

- Hacer commit y push de la rama revisada con los cambios de logica.
- Configurar y desplegar AWS con PostgreSQL RDS y URL publica.
- Implementar healthcheck requerido para evidenciar disponibilidad en AWS.
- Ejecutar la coleccion Postman contra la URL desplegada.
- Completar esta documentacion con integrantes, enlaces, capturas y conclusiones finales.
