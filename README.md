# Coworking Reservations API

API REST para la gestión de reservas de espacios de coworking (salas de reuniones, puestos de trabajo, oficinas privadas y auditorios). Controla el solapamiento de horarios, valida el pago contra un servicio externo inestable y expone un reporte de ocupación cacheado.

> **Nota:** este README documenta lo que está implementado y lo que quedó fuera por el plazo de cuatro días. Ver la sección [Fuera de alcance](#fuera-de-alcance).

---

## Tabla de Contenidos

- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Prerrequisitos](#prerrequisitos)
- [Configuración y Ejecución](#configuración-y-ejecución)
- [Cómo Funciona](#cómo-funciona)
- [Decisiones Técnicas](#decisiones-técnicas)
- [Migraciones (Flyway)](#migraciones-flyway)
- [Trade-offs asumidos](#trade-offs-asumidos)
- [Fuera de alcance](#fuera-de-alcance)

---

## Tecnologías

- Java 21
- Spring Boot 4.0.7
- Spring Security + JWT (JJWT)
- Spring Data JPA / Hibernate 7
- PostgreSQL 18
- Flyway
- Resilience4j (Circuit Breaker)
- Caffeine (caché)
- WireMock (servicio de pago simulado)
- Gradle
- Docker & Docker Compose

---

## Arquitectura

Arquitectura en capas organizada por feature: cada módulo contiene su propio `controller`, `service`, `repository`, `domain` y `dto`.

```
com.pereira.api
├── shared/         config, excepciones comunes, BaseEntity
├── security/       JWT, filtro, autenticación
├── usuario/        registro
├── espacio/        CRUD
├── reserva/        creación, cancelación, confirmación
│   ├── state/      patrón State del ciclo de vida
│   └── event/      eventos de dominio
├── pago/           cliente HTTP + circuit breaker
├── notificacion/   listener asíncrono
└── reporte/        ocupación con caché
```

Reglas fijas en todos los features:

- El controlador solo habla con DTOs, nunca con entidades.
- La transacción vive en el servicio (`@Transactional`), nunca en el controlador.
- Las relaciones `@ManyToOne` son `LAZY` explícitas; los listados resuelven con `join fetch`.
- `open-in-view` desactivado: obliga a resolver relaciones lazy dentro del servicio.
- `ddl-auto: validate`: el esquema lo define Flyway, Hibernate solo verifica.

---

## Prerrequisitos

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)

No hace falta tener Java, Gradle ni PostgreSQL instalados localmente — todo corre en contenedores.

---

## Configuración y Ejecución

1. **Clona el repositorio**:

   ```bash
   git clone https://github.com/jl24pereira/reservations-api.git
   cd reservations-api
   ```

2. **Levanta todo con Docker Compose**:

   ```bash
   docker compose up -d --build
   ```

   Esto levanta tres contenedores:
   - `reservations-db`: PostgreSQL 18, con esquema y datos de prueba aplicados vía Flyway.
   - `reservations-payments`: WireMock simulando el servicio externo de validación de pago, en `http://localhost:8081`.
   - `reservations-api`: la API, en `http://localhost:8080`.

3. **Verifica que arrancó**:

   ```bash
   curl http://localhost:8080/actuator/health
   ```

4. **Detener los contenedores**:

   ```bash
   docker compose down
   ```

5. **Reiniciar desde una base limpia** (borra los datos):

   ```bash
   docker compose down -v
   docker compose up -d --build
   ```

6. **Modo desarrollo** (infraestructura en Docker, la API desde el IDE):

   ```bash
   docker compose up -d db payments
   cd api && ./gradlew bootRun
   ```

   El perfil `dev` apunta a `localhost` para la base y para WireMock.

### Usuario inicial

| Email                 | Password   | Rol   |
| --------------------- | ---------- | ----- |
| `admin@coworking.com` | `admin123` | ADMIN |

Los usuarios `USER` se crean con `POST /auth/register`. El registro fuerza el rol `USER`: no es posible autoasignarse `ADMIN`.

---

## Cómo Funciona

### Obtener un token

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@coworking.com", "password": "admin123"}'
```

La respuesta incluye el token, el tipo (`Bearer`) y la fecha de expiración. Todas las peticiones siguientes usan `Authorization: Bearer <token>`.

### Registrar un usuario

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "usuario@correo.com", "password": "Clave1234!", "nombre": "Usuario Demo"}'
```

### Crear un espacio (solo ADMIN)

```bash
curl -X POST http://localhost:8080/espacios \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token-admin>" \
  -d '{
    "nombre": "Sala Ceiba",
    "tipo": "SALA_REUNIONES",
    "capacidad": 8,
    "ubicacion": "Piso 1 - Ala Norte",
    "tarifaHora": 15.00
  }'
```

### Crear una reserva

```bash
curl -X POST http://localhost:8080/reservas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token-user>" \
  -d '{
    "espacioId": "<uuid-del-espacio>",
    "inicio": "2026-12-01T10:00:00-06:00",
    "fin": "2026-12-01T12:00:00-06:00"
  }'
```

La reserva nace en estado `PENDING` con el `montoTotal` ya calculado a partir de la tarifa vigente del espacio. Si el horario choca con otra reserva activa, devuelve `409`. Una reserva de 12:00–14:00 **sí** se permite, porque los rangos son semiabiertos.

### Simulación del servicio de pago

El servicio externo de validación de pago está simulado con WireMock. Los stubs viven en
`wiremock/mappings/` y se seleccionan según el valor de `paymentMethod` que envíe la
petición de confirmación, lo que permite provocar cada escenario a voluntad sin tocar
configuración ni reiniciar nada.

| `paymentMethod`              | Stub                    | Respuesta del mock                                         | Efecto en la reserva                                           |
| ---------------------------- | ----------------------- | ---------------------------------------------------------- | -------------------------------------------------------------- |
| `VISA`, `MASTERCARD`, u otro | `01-pago-aprobado.json` | `200` con `approved: true` y un `authorizationId` generado | `CONFIRMED`                                                    |
| `SLOW`                       | `02-pago-lento.json`    | `200`, pero tras 8 segundos                                | `PENDING_PAYMENT` (timeout de lectura a los 3s)                |
| `FAIL`                       | `03-pago-error.json`    | `503 Service Unavailable`                                  | `PENDING_PAYMENT` (fallo contabilizado por el circuit breaker) |

La selección se resuelve por prioridad de WireMock: los stubs de fallo declaran
`"priority": 1` y filtran por un `paymentMethod` concreto; el stub de éxito declara
`"priority": 10` y actúa como caso por defecto para cualquier otro valor. ww

**Escenario de éxito**

```bash
curl -X POST http://localhost:8080/reservas/<reservaId>/confirmar \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"paymentMethod": "VISA"}'
```

La reserva pasa a `CONFIRMED`, se registra el pago con su `authorizationId` y se publica
el evento de dominio que dispara la notificación asíncrona.

**Escenario de servicio lento**

```bash
curl -X POST http://localhost:8080/reservas/<reservaId>/confirmar \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"paymentMethod": "SLOW"}'
```

El mock tarda 8 segundos, pero el cliente corta a los 3 por el `readTimeout` configurado.
El circuit breaker contabiliza el corte como fallo y la reserva queda en `PENDING_PAYMENT`.
Sin ese timeout, una respuesta lenta nunca se convertiría en fallo y el circuito jamás
llegaría a abrirse.

**Escenario de error del proveedor**

```bash
curl -X POST http://localhost:8080/reservas/<reservaId>/confirmar \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"paymentMethod": "FAIL"}'
```

El mock devuelve `503`. La petición HTTP a la API responde `200` de todas formas: el
fallback degrada el resultado a `PENDING_PAYMENT` en lugar de propagar el error al cliente.

### Demostrar el circuit breaker

Tres confirmaciones seguidas con `FAIL` sobre reservas distintas superan el umbral configurado y abren el circuito:

```bash
curl http://localhost:8080/actuator/circuitbreakers \
  -H "Authorization: Bearer <token-admin>"
```

Con el circuito en `OPEN`, incluso una confirmación con `VISA` —que funcionaría— cae al fallback y deja la reserva en `PENDING_PAYMENT`, sin llegar a llamar al servicio externo. Tras 10 segundos transiciona a `HALF_OPEN` y se recupera solo.

### Consultar la ocupación

```bash
curl "http://localhost:8080/reportes/ocupacion?desde=2026-12-01T00:00:00-06:00&hasta=2026-12-31T23:59:59-06:00" \
  -H "Authorization: Bearer <token-admin>"
```

La primera llamada consulta la base; las siguientes se sirven del caché hasta que una reserva se confirme o cancele.

### Cancelar una reserva

```bash
curl -X DELETE http://localhost:8080/reservas/<reservaId> \
  -H "Authorization: Bearer <token-user>"
```

Un segundo intento devuelve `409`: `CANCELLED` es un estado terminal y la máquina de estados rechaza la transición.

### Endpoints completos

| Método              | Ruta                                     | Acceso                           |
| ------------------- | ---------------------------------------- | -------------------------------- |
| POST                | `/auth/register`                         | Público                          |
| POST                | `/auth/login`                            | Público                          |
| GET                 | `/espacios`                              | Autenticado                      |
| GET                 | `/espacios/{id}`                         | Autenticado                      |
| POST / PUT / DELETE | `/espacios` · `/espacios/{id}`           | ADMIN                            |
| POST                | `/reservas`                              | Autenticado                      |
| GET                 | `/reservas`                              | USER ve las propias, ADMIN todas |
| GET                 | `/reservas/{id}`                         | Propias / ADMIN                  |
| POST                | `/reservas/{id}/confirmar`               | Propias / ADMIN                  |
| DELETE              | `/reservas/{id}`                         | Propias / ADMIN                  |
| GET                 | `/reportes/ocupacion`                    | ADMIN                            |
| GET                 | `/actuator/health` · `/info`             | Público                          |
| GET                 | `/actuator/metrics` · `/circuitbreakers` | ADMIN                            |

Documentación interactiva: `http://localhost:8080/swagger-ui.html`
Colección de Postman: `postman/coworking-reservations-api.postman_collection.json`

### Ciclo de vida de una reserva

```
PENDING ──confirmar──> CONFIRMED ──completar──> COMPLETED
   │                        │
   │                        └──cancelar──> CANCELLED
   ├──pago no disponible──> PENDING_PAYMENT ──confirmar──> CONFIRMED
   │                                │
   └──cancelar──> CANCELLED         └──cancelar──> CANCELLED
```

`CANCELLED` y `COMPLETED` son terminales.

---

## Decisiones Técnicas

### Spring Boot 4.0.7 en lugar de 3.x

El enunciado especifica Spring Boot 3.x. Se optó por 4.0.7 porque toda la rama 3.x alcanzó fin de vida en junio de 2026 y ya no recibe parches de seguridad. Entregar una base "lista para producción" sobre una versión sin soporte sería contradictorio con el objetivo del ejercicio.

Se eligió 4.0.7 y no 4.1.x porque el tren Spring Cloud 2025.1.2 declara 4.0.7 como su versión soportada de referencia; con 4.1.0 solo "introduce compatibilidad". En un componente crítico como el circuit breaker se prefirió la combinación probada.

### Solapamiento: exclusion constraint en PostgreSQL

**El punto técnico central.** El requisito de no permitir reservas solapadas no puede garantizarse solo con `@Transactional` y una consulta previa: dos peticiones concurrentes pueden verificar simultáneamente que el horario está libre y ambas insertar.

La invariante vive en la base de datos:

```sql
constraint ex_reserva_solapada exclude using gist (
    espacio_id with =,
    tstzrange(inicio, fin) with &&
) where (estado <> 'CANCELLED')
```

Esto la hace imposible de violar incluso con varias instancias del servicio corriendo. Requiere la extensión `btree_gist` porque combina un operador de igualdad con uno de solapamiento en el mismo índice GiST. El `tstzrange` es un rango semiabierto, de modo que reservas consecutivas (12:00–14:00 tras 10:00–12:00) no se consideran solapadas.

En la aplicación se mantiene además una verificación previa que devuelve un `409` limpio en el caso normal. La constraint actúa como red de seguridad: al violarse, la `DataIntegrityViolationException` se inspecciona por el nombre de la constraint y se traduce al mismo `409`. Se usa `saveAndFlush` en lugar de `save` para que el INSERT ocurra dentro del bloque donde puede capturarse.

**Alternativa descartada:** bloqueo pesimista sobre la fila del espacio. Funciona, pero serializa todas las reservas de un mismo espacio aunque sean de fechas distintas, y no protege ante escrituras que no pasen por la aplicación.

### Patrón de comportamiento: State

Aplicado al ciclo de vida de la reserva. La interfaz `ReservaState` declara las transiciones con implementaciones por defecto que las rechazan; cada estado concreto sobrescribe solo las que permite. `CanceladaState` y `CompletadaState` no sobrescriben nada: su ausencia de código _es_ la regla de negocio.

**Qué resuelve frente a `if/else` o `switch`:** con un condicional, las reglas quedan dispersas en cada método que modifica el estado, y agregar un estado obliga a revisar todos esos puntos sin ayuda del compilador — es fácil olvidar uno y dejar un agujero silencioso. Con State, cada estado declara sus transiciones en un único lugar y el comportamiento por defecto garantiza que lo no declarado se rechace.

La entidad delega en el patrón, de modo que el servicio nunca escribe `setEstado(...)`:

```java
public void confirmar() {
    this.estado = ReservaStateFactory.de(this.estado).confirmar();
}
```

**Patrón secundario: Observer**, vía `ApplicationEventPublisher` (ver más abajo).

### Spring Security con JWT

Configuración stateless con CSRF deshabilitado. **CSRF se desactiva porque no hay cookies de sesión que proteger**.

El `JwtAuthenticationFilter` extiende `OncePerRequestFilter` y nunca rechaza: si el token falta o es inválido, no autentica y deja que `AuthorizationFilter` decida.

**El rol viaja como claim, pero no es la fuente de autorización.** El filtro carga el usuario desde la base y de ahí salen las authorities. Confiar solo en el claim implicaría que degradar a un usuario de ADMIN a USER no tuviera efecto hasta que expirara su token.

Decisiones adicionales:

- El registro fuerza `Rol.USER`; el DTO no expone el campo.
- Credenciales inválidas devuelven un mensaje genérico, sin distinguir entre email inexistente y contraseña incorrecta, para impedir enumeración de usuarios.
- Una petición a una reserva ajena devuelve **404, no 403**: un 403 confirmaría que el recurso existe.
- Actuator más allá de `health` e `info` requiere ADMIN.
- `@PreAuthorize` se aplica también a nivel de método, como defensa en profundidad.
- Se distinguen **401** y **403** mediante `AuthenticationEntryPoint` y `AccessDeniedHandler` propios, que escriben la respuesta a mano porque se ejecutan en la cadena de filtros, fuera del alcance del `@ControllerAdvice`.

### Circuit Breaker con Resilience4j

```yaml
resilience4j:
  circuitbreaker:
    instances:
      pago:
        sliding-window-size: 10
        minimum-number-of-calls: 5
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
        automatic-transition-from-open-to-half-open-enabled: true
```

Los umbrales están ajustados para ser **demostrables**: con los valores por defecto (ventana de 100 llamadas) harían falta decenas de peticiones para ver abrirse el circuito.

**El fallback devuelve un resultado de dominio, no lanza excepción.** Ante fallo o circuito abierto, la reserva queda en `PENDING_PAYMENT` y la petición HTTP responde normalmente.

### Transaccionalidad: la llamada externa fuera de la transacción

Confirmar una reserva implica una llamada HTTP a un servicio que puede tardar. Mantener la transacción abierta durante esa llamada sostendría locks durante segundos y, bajo carga, agotaría el pool de conexiones por un fallo ajeno.

El flujo se divide en tres pasos:

1. **Transacción corta** — valida permisos y estado, obtiene el monto.
2. **Sin transacción** — llamada al servicio de pago, protegida por el circuit breaker.
3. **Transacción corta** — registra el pago y aplica la transición de estado.

Esto exigió **dos beans separados** (`ConfirmacionReservaService` y `ConfirmacionTxService`), porque las anotaciones basadas en AOP solo actúan sobre llamadas que cruzan el límite del bean: un método `@Transactional` invocado desde otro método de la misma clase se ejecuta sin transacción, silenciosamente. Es una concesión al funcionamiento del framework, pero es la única forma de conseguir transacciones cortas reales.

### Caché del reporte

`@Cacheable` sobre el endpoint de ocupación, con Caffeine.

**Por qué Caffeine y no el `ConcurrentMapCacheManager` por defecto:** el predeterminado no soporta TTL ni límite de tamaño. Como la clave depende del rango de fechas solicitado, un cliente podría generar entradas ilimitadas variando el rango — un vector de agotamiento de memoria. Se configuró `maximumSize(200)` y `expireAfterWrite(10m)`.

La invalidación real ocurre por eventos (`@CacheEvict` en los listeners de reserva confirmada y cancelada); el TTL queda como red de seguridad ante cambios que no pasen por la aplicación.

### Consultas y prevención de N+1

Relaciones `@ManyToOne` declaradas `LAZY` explícitamente (el valor por defecto de JPA es `EAGER`, causa habitual de N+1). Los listados usan `join fetch` con `countQuery` separada, obligatoria al combinar paginación con fetch.

Los filtros opcionales se resuelven con el patrón `:parametro is null or condicion`. Se descartó `Specifications` porque con cinco filtros fijos añade complejidad sin beneficio; sería la opción correcta ante filtros verdaderamente dinámicos.

El reporte de ocupación usa **SQL nativo** con `extract(epoch from (fin - inicio))`, agregando en la base en lugar de traer filas a memoria. El `left join` con las condiciones en el `on` (no en el `where`) asegura que los espacios sin reservas aparezcan con ocupación cero en lugar de desaparecer.

### Validación y manejo de errores

Bean Validation en los DTOs de entrada, con un `@RestControllerAdvice` que centraliza la traducción usando `ProblemDetail` (RFC 7807).

| Excepción                         | Código                   |
| --------------------------------- | ------------------------ |
| `ReservaSolapadaException`        | 409                      |
| `TransicionInvalidaException`     | 409                      |
| `RangoInvalidoException`          | 400                      |
| `MethodArgumentNotValidException` | 400 + lista de errores   |
| `*NoEncontradoException`          | 404                      |
| `EmailYaRegistradoException`      | 409                      |
| `CredencialesInvalidasException`  | 401                      |
| `Exception` genérica              | 500 sin exponer detalles |

`TransicionInvalidaException` devuelve **409 y no 400** deliberadamente: la petición está bien formada; lo que ocurre es que el recurso está en un estado incompatible con la operación.

### Configuración por perfiles

`application.yml` contiene lo común; `application-dev.yml` y `application-prod.yml` solo lo que difiere. Spring fusiona ambos, de modo que duplicar valores obligaría a mantenerlos sincronizados en varios sitios.

Toda la configuración propia se enlaza con `@ConfigurationProperties` sobre records inmutables, sin `@Value` disperso:

```java
@ConfigurationProperties(prefix = "app.pago")
public record PagoProperties(String baseUrl, Duration connectTimeout, Duration readTimeout) {}
```

En `prod`, credenciales y secreto JWT provienen de variables de entorno **sin valor por defecto**: si falta una, la aplicación no arranca. El `datasource` no está en el archivo base a propósito — no existe un valor sensato para todos los ambientes, y definir uno haría que un perfil mal escrito conectara silenciosamente a la base equivocada.

### Docker

`Dockerfile` multi-stage: la primera etapa compila con JDK, la segunda copia solo el jar sobre una imagen JRE. El contenedor corre bajo un usuario sin privilegios, no como root. Las dependencias se copian antes que el código fuente para que Docker cachee esa capa.

En el compose, el servicio `api` depende del _healthcheck_ de PostgreSQL (`condition: service_healthy`), no de que el contenedor exista: sin eso, Flyway intenta migrar contra una base que aún no acepta conexiones.

---

## Migraciones (Flyway)

El esquema no se crea a mano ni con `ddl-auto`: vive versionado en `api/src/main/resources/db/migration/`. Flyway lo aplica en orden cada vez que arranca la API, y `ddl-auto: validate` verifica que las entidades JPA coincidan exactamente con las tablas creadas.

| Archivo              | Contenido                                                                    |
| -------------------- | ---------------------------------------------------------------------------- |
| `V1__schema.sql`     | Las 4 tablas, extensión `btree_gist`, exclusion constraint, checks e índices |
| `V2__datos_demo.sql` | Usuario ADMIN y seis espacios de ejemplo                                     |

Los `V__` corren una sola vez y no pueden modificarse después de aplicados (Flyway valida por checksum). Durante el desarrollo, el botón de reinicio es `docker compose down -v`.

**No se siembran reservas** a propósito: tendrían fechas fijas que envejecen, y en pocas semanas todas estarían en el pasado, dejando el reporte de ocupación en cero.

Detalles del esquema que vale la pena señalar:

- **Todas las marcas temporales son `timestamptz`**: con reservas por franja horaria, almacenar sin zona produce errores en cuanto servidor y cliente difieren.
- **UUID como clave primaria**, generados con `uuidv7()` (función nativa desde PostgreSQL 18). No revelan volumen de negocio ni permiten enumerar recursos ajenos, y a diferencia de UUIDv4 mantienen los índices ordenados temporalmente.
- **`on delete restrict`** entre reserva y espacio: un espacio con reservas no se borra, se desactiva. El `DELETE` de espacios es una baja lógica.

---

## Fuera de alcance

Lo que quedó pendiente y se abordaría en una siguiente iteración:

- **Pruebas.** Es la deuda principal: faltan los tests unitarios de la máquina de estados y del cálculo de ocupación, y las dos pruebas de integración previstas — solapamiento concurrente con Testcontainers y apertura del circuito con WireMock embebido.
- **Anotaciones OpenAPI** en los controladores. Swagger se genera por introspección, pero sin descripciones ni ejemplos por operación.
- **Refresh tokens y revocación.** El token dura una hora y no hay renovación; un token comprometido es válido hasta expirar.
- **Invalidación selectiva del caché**, calculando qué rangos afecta cada reserva en lugar de vaciar todas las entradas.
- **Horarios de disponibilidad por espacio**, que harían el reporte de ocupación más representativo.
- **DTO propio de paginación**, desacoplando el contrato de Spring Data.
- **Auditoría** de cambios de estado, con usuario y momento de cada transición.
- **Reintentos con backoff** sobre el servicio de pago, complementando el circuit breaker para fallos transitorios.
- **Rate limiting** en los endpoints de autenticación, para mitigar fuerza bruta.
- **Deshabilitar Swagger en producción** (`springdoc.api-docs.enabled: false`), pendiente en `application-prod.yml`.

