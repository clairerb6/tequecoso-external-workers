# Flowable External Worker

Aplicación Spring Boot que implementa workers externos para Flowable y delega la lógica de negocio a servicios REST.

## Objetivo

Este repositorio desacopla tareas de proceso BPMN desde Flowable hacia workers Java que consumen endpoints HTTP externos. La idea es que Flowable publique jobs por `topic` y este servicio los tome, ejecute la integración REST correspondiente y devuelva variables al proceso.

## Stack técnico

- Java 21
- Spring Boot 4.1.0
- Flowable External Worker Client 2.0.0
- Spring WebFlux (`WebClient`)
- Maven

## Estructura del proyecto

```text
src/main/java/cl/tequecoso/worker
├── ExternalWorkerApplication.java
├── commonInterface
│   └── ExternalWorkerHandler.java
├── model
│   └── Cliente.java
└── worker
    └── SearchClient.java
```

## Requisitos

- Java 21 o superior
- Maven 3.9+
- Una instancia de Flowable accesible por HTTP
- Acceso al backend REST configurado en `servicios.endpoint-url`

## Configuración

Archivo: [`src/main/resources/application.yml`](src/main/resources/application.yml)

```yml
flowable:
  base-url: http://localhost:8080
  username: admin
  password: test

servicios:
  endpoint-url: https://tomcat.katherineflores.me/webservice-tequecoso/api/v1/
```

### Consideraciones de configuración

- `flowable.base-url`: URL base de la instancia de Flowable que expone external workers.
- `flowable.username` / `flowable.password`: credenciales de acceso.
- `servicios.endpoint-url`: URL base del backend REST externo.

Si Flowable no corre en tu máquina local, debes apuntar `flowable.base-url` al ambiente real correspondiente.

## Ejecución

Compilar:

```bash
mvn clean compile
```

Ejecutar:

```bash
mvn spring-boot:run
```

## Cómo funciona un worker en este proyecto

Cada worker:

1. Se registra con `@FlowableWorker(topic = "...")`
2. Recibe variables del job desde Flowable
3. Valida las variables requeridas
4. Llama a un servicio REST externo
5. Devuelve variables nuevas o actualizadas al proceso BPMN

La clase principal habilita el escaneo de workers con `@EnableFlowableWorker`.

## Workers implementados

### 1. `SearchClient`

Archivo: [`src/main/java/cl/tequecoso/worker/worker/SearchClient.java`](src/main/java/cl/tequecoso/worker/worker/SearchClient.java)

**Topic de Flowable**

```text
search_client
```

**Objetivo**

Buscar un cliente existente por RUT en un servicio REST externo.

**Entrada esperada desde Flowable**

- `rut`

Tipos soportados para `rut`:

- `String`
- `Integer`
- `Long`
- Cualquier subtipo de `Number`

**Llamada REST**

```http
GET /client/rut/{rut}
```

URL efectiva:

```text
{servicios.endpoint-url}/client/rut/{rut}
```

**Respuesta esperada del backend**

Un JSON compatible con el modelo `Cliente`.

**Variables de salida hacia Flowable**

Si el cliente existe:

- `exists = true`
- `client = <id del cliente>`

Si el cliente no existe (`404`):

- `exists = false`
- `client = null`

Si falta la variable `rut`:

- el worker responde con `failure`

Si ocurre otro error técnico:

- el worker responde con `failure`
- se envía el mensaje `Error al buscar cliente`

### Consideraciones específicas de `SearchClient`

- El proceso BPMN debe enviar la variable `rut` antes de publicar el job del topic `search_client`.
- El worker no crea clientes; solo consulta uno existente.
- El backend REST debe devolver `404` cuando no encuentra cliente. Ese caso se interpreta como una condición funcional válida, no como error del proceso.
- Si el backend devuelve otro código de error (`500`, `401`, `403`, etc.), el worker marcará la tarea como fallo técnico.
- La variable `client` contiene solo el `id` del cliente, no el objeto completo.

## Contrato recomendado para futuros workers

Si agregas más workers, conviene mantener este criterio:

### 1. Un worker por responsabilidad

Cada clase en `worker/` debe resolver una sola tarea del proceso.

### 2. Topic explícito y estable

El valor del `topic` en `@FlowableWorker` debe coincidir exactamente con el topic configurado en BPMN.

### 3. Validación de entrada

Cada worker debe validar al inicio:

- variables obligatorias
- tipos soportados
- valores vacíos o inválidos

Si falta una variable crítica, debe responder con `failure`.

### 4. Salida pequeña y útil

Devuelve a Flowable solo las variables necesarias para continuar el proceso. Evita enviar objetos grandes si el BPMN solo necesita un identificador o un flag booleano.

### 5. Manejo explícito de errores

Diferencia entre:

- error funcional esperado, por ejemplo `404`
- error técnico real, por ejemplo timeout, `500`, problema de autenticación o parseo

### 6. Contrato REST documentado

Por cada worker nuevo, documenta en este `README`:

- nombre de la clase
- topic
- variables de entrada
- endpoint consumido
- variables de salida
- casos de error

## Modelo de datos

### `Cliente`

Archivo: [`src/main/java/cl/tequecoso/worker/model/Cliente.java`](src/main/java/cl/tequecoso/worker/model/Cliente.java)

Representa el cliente devuelto por el backend REST. Actualmente el worker usa principalmente:

- `id`
- `rut`

El resto de los campos quedan disponibles si en el futuro otro worker necesita más datos del cliente.

## Desarrollo

Para agregar un nuevo worker:

1. Crear una clase en `src/main/java/cl/tequecoso/worker/worker`
2. Implementar `ExternalWorkerHandler`
3. Anotar el método con `@FlowableWorker(topic = "nuevo_topic")`
4. Leer y validar variables desde `job.getVariables()`
5. Consumir el endpoint externo necesario
6. Devolver `success()` o `failure()` según el resultado
7. Documentar el contrato del worker en este `README`

## Estado actual

Actualmente el repositorio implementa un solo worker:

- `SearchClient`

Si se agregan más workers, este `README` debería ampliarse con una sección por cada uno.
