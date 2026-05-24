# PLAN.md - carpULTEC Backend MVP

## Resumen

Backend Spring Boot para carpool universitario UTEC. El modelo base ya existe y se mantiene sin herencia: `Publication` representa la intención inicial de viaje, `RequestPublication` representa la respuesta a una publicación, y `Ride` representa un viaje confirmado generado desde solicitudes aceptadas.

Las capas actuales (`controller`, `service`, `repository`, `dto`) existen, pero están en modo CRUD genérico. Falta implementar reglas de negocio, seguridad JWT, ownership, excepciones, eventos simples de registro y soporte inicial de coordenadas.

## Decisiones Cerradas

- `RequestPublication.status` usará enum, no `String`.
- Valores del enum: `PENDING`, `ACCEPTED`, `REJECTED`, `CANCELLED`.
- JWT tendrá registro/login con password propio y password hash.
- Solo usuarios con correo `@utec.edu.pe` pueden registrarse.
- `pickupPointOrDestine` será obligatorio al crear una `RequestPublication`.
- Al aceptar una solicitud, el request enviará `vehicleId`.
- El backend determinará el conductor real y validará que el vehículo pertenezca a ese conductor.
- `Ride` se crea o actualiza al aceptar una `RequestPublication`.
- Publicación de conductor puede aceptar múltiples pasajeros hasta llenar cupos.
- Publicación de pasajero acepta un conductor y cierra el flujo.
- Eventos por hora de salida o botón "me voy" quedan fuera del MVP.
- CRUD directo de `Ride` y `RidePassenger` se mantiene restringido; el flujo público normal será aceptar solicitudes.

## Reglas De Negocio MVP

`Publication`:
- `fromUTEC = true`: el viaje sale desde UTEC.
- `fromUTEC = false`: el viaje va hacia UTEC.
- `driverToPassenger = true`: el autor es conductor y ofrece asientos.
- `driverToPassenger = false`: el autor es pasajero y busca conductor.
- `destinationOrOrigin` cambia de significado según `fromUTEC` y `driverToPassenger`.
- `departureTime` debe ser futura.
- `seats` debe ser mayor o igual a 1.

### Semántica De `destinationOrOrigin`

| driverToPassenger | fromUTEC | Significado |
|---|---|---|
| true | false | Origen externo del conductor hacia UTEC |
| true | true | Destino externo del conductor saliendo desde UTEC |
| false | false | Punto externo donde el pasajero quiere ser recogido hacia UTEC |
| false | true | Destino externo del pasajero saliendo desde UTEC |

`RequestPublication`:
- El requester no puede ser el autor de la publicación.
- Debe existir como máximo una solicitud activa por requester y publicación.
- `requesterIsDriver = true`: el requester se ofrece como conductor.
- `requesterIsDriver = false`: el requester pide asiento como pasajero.
- `pickupPointOrDestine` es obligatorio.
- `seats` representa asientos ofrecidos si el requester es conductor, o asientos solicitados si es pasajero.
- Solo el autor de la publicación puede aceptar o rechazar solicitudes.
- Solo el requester puede cancelar su propia solicitud.
- Solo solicitudes `PENDING` pueden aceptarse, rechazarse o cancelarse.

### Semántica De `pickupPointOrDestine`

| requesterIsDriver | fromUTEC | Significado |
|---|---|---|
| true | false | Punto donde el conductor recogerá al pasajero hacia UTEC |
| true | true | Punto donde el conductor dejará al pasajero saliendo desde UTEC |
| false | false | Punto donde el pasajero espera ser recogido hacia UTEC |
| false | true | Punto donde el pasajero desea bajarse saliendo desde UTEC |

`Ride`:
- Si no existe ride para la publicación al aceptar, se crea.
- Si ya existe ride para una publicación de conductor, se agrega el pasajero aceptado.
- Se valida cupo disponible antes de aceptar.
- Si la publicación era de pasajero, aceptar un conductor crea el ride y no se aceptan más conductores.

## Slices De Implementación

1. **Estados y excepciones**
   - Normalizar `Status` a enum con valores en mayúsculas.
   - Cambiar `RequestPublication.status` de `String` a enum.
   - Evitar que el DTO de creación controle directamente el estado.
   - Agregar excepciones de negocio: `BusinessRuleException`, `ForbiddenException`, `DuplicateResourceException`, `UnauthorizedException`.
   - Ampliar `RestExceptionHandler` con respuestas consistentes.

2. **Registro, login y JWT**
   - Agregar password hash a `User`.
   - Crear endpoints `POST /api/auth/register` y `POST /api/auth/login`.
   - Validar dominio `@utec.edu.pe`.
   - Emitir JWT y resolver usuario autenticado.
   - Agregar `GET /api/users/me`.

3. **Ownership y DTOs seguros**
   - Dejar de confiar en `authorId` y `requesterId` cuando exista JWT.
   - Crear publicaciones con el usuario autenticado.
   - Crear solicitudes con el usuario autenticado.
   - Proteger actualización/eliminación de recursos por ownership.
   - Mantener CRUD sensible restringido.

4. **Flujo de solicitudes**
   - Crear `POST /api/publications/{publicationId}/requests`.
   - Crear `GET /api/publications/{id}/requests`.
   - Crear `PATCH /api/request-publications/{id}/cancel`.
   - Crear `PATCH /api/request-publications/{id}/reject`.
   - Validar duplicados, roles, ownership y estados.

5. **Aceptación y Ride**
   - Crear `PATCH /api/request-publications/{id}/accept` con `vehicleId`.
   - Determinar conductor real según publicación y solicitud.
   - Validar que el vehículo pertenezca al conductor.
   - Crear o actualizar `Ride`.
   - Crear `RidePassenger` cuando corresponda.
   - Validar cupos y cierre de flujo para publicaciones de pasajero.

6. **Google Maps MVP**
   - No crear entidad nueva para ubicación.
   - Agregar coordenadas mínimas a `Publication` y `RequestPublication`.
   - Guardar coordenadas del punto variable externo a UTEC.
   - Mantener coordenadas de UTEC como configuración.
   - Implementar distancia simple por latitud/longitud.
   - Dejar matching avanzado y optimización de rutas fuera del MVP.

7. **Correo de registro**
   - Agregar envío de correo al registrarse.
   - No requerir verificación por email en el MVP.
   - Manejarlo como evento/asíncrono simple para no bloquear el registro.

## Endpoints Principales MVP

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/users/me`
- `GET /api/publications`
- `POST /api/publications`
- `GET /api/publications/{id}`
- `GET /api/publications/{id}/requests`
- `POST /api/publications/{publicationId}/requests`
- `PATCH /api/request-publications/{id}/accept`
- `PATCH /api/request-publications/{id}/reject`
- `PATCH /api/request-publications/{id}/cancel`
- `GET /api/rides/my`

## Fuera Del MVP: No Implementar Todavía

- Matching avanzado.
- Optimización de rutas.
- Creación de `Ride` por hora de salida.
- Botón "me voy" para iniciar viaje.
- Estados avanzados de `Ride`.
- Verificación obligatoria por correo.
- Roles/permisos complejos.
- Entidad `Location`, salvo que luego aparezcan favoritos, historial, múltiples paradas o auditoría de ubicaciones.

## Tests Y Criterios De Aceptación

- Tests de servicios para estados de `RequestPublication`.
- Tests de validación de correo `@utec.edu.pe`.
- Tests de ownership: autor acepta/rechaza, requester cancela.
- Tests de aceptación con `vehicleId` válido e inválido.
- Tests de cupos para publicación de conductor.
- Tests de publicación de pasajero aceptando solo un conductor.
- Tests de endpoints principales con usuario autenticado.
- El proyecto debe compilar y pasar tests con JDK 17.

## Importante
- Prioriza soluciones simples y fáciles de mantener sobre diseños excesivamente abstractos.
- Evita introducir patrones complejos si el problema puede resolverse con una solución más directa.