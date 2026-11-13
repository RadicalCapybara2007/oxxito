# Oxxito

Microservicio REST para administrar productos. Está desarrollado con Java 21 y Spring Boot, y utiliza MongoDB para persistir la información.

## Contenido

- [Tecnologías y arquitectura](#tecnologías-y-arquitectura)
- [Requisitos](#requisitos)
- [Configuración de MongoDB](#configuración-de-mongodb)
- [Ejecución local](#ejecución-local)
- [API REST](#api-rest)
- [Validaciones y errores](#validaciones-y-errores)
- [Pruebas y cobertura con JaCoCo](#pruebas-y-cobertura-con-jacoco)
- [Análisis con SonarQube](#análisis-con-sonarqube)
- [Docker](#docker)
- [Estado respecto a la rúbrica](#estado-respecto-a-la-rúbrica)

## Tecnologías y arquitectura

- Java 21
- Spring Boot 4.1.2
- Spring Web para los endpoints REST
- Spring Data MongoDB para persistencia
- Jakarta Bean Validation para validar solicitudes
- JUnit, Mockito y Spring MockMvc para pruebas
- JaCoCo para generar el reporte de cobertura
- SonarQube Maven Plugin para enviar el análisis estático

La aplicación organiza el código en controlador, servicio, repositorio, modelo y objetos DTO. La entidad `Producto` se guarda en la colección MongoDB `productos`; el repositorio extiende `MongoRepository`.

## Requisitos

- JDK 21
- Docker y Docker Compose (opcional, para ejecutar MongoDB o la aplicación en contenedores)
- Maven no es necesario si se utiliza el Maven Wrapper incluido (`./mvnw`)

## Configuración de MongoDB

La URI predeterminada está declarada en `src/main/resources/application.properties`:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/oxxito_db
```

Inicia MongoDB localmente con Docker y conserva los datos en un volumen:

```bash
docker volume create oxxito-mongo-data
docker run -d --name oxxito-mongo \
  -p 27017:27017 \
  -v oxxito-mongo-data:/data/db \
  mongo:7
```

Si ya existe un contenedor con ese nombre, puedes iniciarlo con `docker start oxxito-mongo`. Para detenerlo usa `docker stop oxxito-mongo`.

Para usar otra base o servidor, establece la propiedad de Spring mediante la variable de entorno `SPRING_DATA_MONGODB_URI`. Por ejemplo:

```bash
SPRING_DATA_MONGODB_URI='mongodb://localhost:27017/oxxito_db' ./mvnw spring-boot:run
```

En un entorno real, configura credenciales y secretos fuera del repositorio. El contenedor MongoDB de los ejemplos es para desarrollo local y no habilita autenticación.

## Ejecución local

Con MongoDB disponible en `localhost:27017`, inicia la aplicación:

```bash
./mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080`. Para compilar y ejecutar todas las pruebas:

```bash
./mvnw clean verify
```

En Windows, utiliza `mvnw.cmd` en lugar de `./mvnw`.

## API REST

Todos los endpoints utilizan el prefijo `/api/productos`. El objeto de entrada y salida tiene esta forma:

```json
{
  "nombre": "Café Andatti",
  "precio": 25.5
}
```

| Método | Ruta | Descripción | Respuesta de éxito |
|---|---|---|---|
| `GET` | `/api/productos` | Lista todos los productos | `200 OK` |
| `GET` | `/api/productos/{id}` | Consulta un producto | `200 OK` |
| `POST` | `/api/productos` | Crea un producto | `201 Created` |
| `PUT` | `/api/productos/{id}` | Actualiza nombre y precio | `200 OK` |
| `DELETE` | `/api/productos/{id}` | Elimina por ID | `204 No Content` |

Ejemplo para crear un producto:

```bash
curl -i -X POST http://localhost:8080/api/productos \
  -H 'Content-Type: application/json' \
  -d '{"nombre":"Café Andatti","precio":25.5}'
```

## Validaciones y errores

El DTO de entrada exige que `nombre` no esté vacío y que `precio` esté presente. Los errores de validación devuelven `400 Bad Request`. Consultar un ID inexistente con `GET` produce `404 Not Found` mediante la excepción de dominio `NotFoundProduct`.

El manejador global devuelve errores JSON con los campos `timestamp`, `status`, `error` y `message`, por ejemplo:

```json
{
  "timestamp": "2026-01-01T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "nombre: El nombre no puede estar vacío, precio: El precio es obligatorio"
}
```

Actualmente no hay una excepción de duplicados ni un manejo específico de conflictos `409`. La validación `@Min` del modelo no forma parte del DTO utilizado por las solicitudes; por tanto, el precio negativo no está rechazado actualmente por el endpoint.

## Pruebas y cobertura con JaCoCo

Las pruebas unitarias de `ProductoService` usan Mockito para simular el repositorio. `ProductoControllerTest` prueba los endpoints con `MockMvc` y un servicio simulado, incluyendo respuestas correctas, validaciones y casos no encontrados. Estas pruebas no conectan con MongoDB ni constituyen pruebas de integración de extremo a extremo.

Ejecuta las pruebas y genera el reporte HTML de JaCoCo:

```bash
./mvnw clean verify
```

Abre `target/site/jacoco/index.html` para revisar la cobertura de instrucciones, líneas y ramas. El reporte XML se genera en `target/site/jacoco/jacoco.xml` y puede utilizarse durante el análisis de SonarQube. La configuración actual genera el reporte, pero no impone un mínimo de cobertura del 80%.

## Análisis con SonarQube

Puedes iniciar SonarQube Community localmente con Docker:

```bash
docker run -d --name oxxito-sonarqube \
  -p 9000:9000 \
  -v oxxito-sonarqube-data:/opt/sonarqube/data \
  -v oxxito-sonarqube-extensions:/opt/sonarqube/extensions \
  -v oxxito-sonarqube-logs:/opt/sonarqube/logs \
  sonarqube:community
```

Espera a que el servidor esté listo en `http://localhost:9000`. Si el puerto 9000 ya está ocupado, cambia el mapeo a `-p 9001:9000` y utiliza `http://localhost:9001` como URL del servidor en los pasos siguientes. Inicia sesión con las credenciales iniciales de SonarQube, cambia la contraseña, crea el proyecto con la clave `oxxito` y genera un token de análisis desde la interfaz.

Define el token en el entorno (no lo guardes en el código ni lo compartas):

```bash
export SONAR_TOKEN='tu-token'
```

Desde la raíz del proyecto, ejecuta las pruebas, genera el reporte JaCoCo y envía el análisis:

```bash
./mvnw clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:3.11.0.3922:sonar \
  -Dsonar.projectKey=oxxito \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token="$SONAR_TOKEN" \
  -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
```

Revisa los resultados y el Quality Gate en el proyecto de SonarQube. El plugin está declarado en `pom.xml`, pero el Quality Gate depende de la configuración del servidor; no se debe considerar aprobado hasta verificar el resultado del análisis.

En Linux, SonarQube puede requerir que el host tenga `vm.max_map_count` configurado en al menos `524288`. Consulta la documentación de SonarQube para los requisitos de Docker y de memoria de la versión utilizada.

## Docker

El proyecto incluye un `Dockerfile` multi-stage y `compose.yaml`. Docker Compose construye la aplicación, levanta MongoDB y conserva la base en un volumen:

```bash
docker compose up --build
```

La API queda disponible en `http://localhost:8080`. Para detener los contenedores:

```bash
docker compose down
```

Los datos de MongoDB permanecen en el volumen `mongo-data`; para eliminar también esos datos de desarrollo:

```bash
docker compose down --volumes
```

En Compose, MongoDB solo es accesible desde la red interna de los contenedores. Si necesitas acceder desde el host, publica el puerto `27017` en `compose.yaml`.

Para construir solo la imagen:

```bash
docker build -t oxxito:local .
```

La imagen por sí sola necesita una instancia MongoDB accesible. Al ejecutarla fuera de Compose, pasa su URI mediante `SPRING_DATA_MONGODB_URI`, por ejemplo:

```bash
docker run --rm -p 8080:8080 \
  -e SPRING_DATA_MONGODB_URI='mongodb://host.docker.internal:27017/oxxito_db' \
  oxxito:local
```

`host.docker.internal` está disponible en Docker Desktop; en Linux configura la dirección del host que corresponda a tu red Docker. No expongas MongoDB ni SonarQube públicamente sin configurar autenticación, permisos y seguridad de red.

## Estado respecto a la rúbrica

| Área | Estado documentado |
|---|---|
| Microservicio REST y persistencia NoSQL | CRUD de productos con Spring Boot y MongoDB implementado. |
| Validaciones y manejo de errores | Validación de nombre/precio requerido, respuesta `400` y excepción `404` para consulta por ID implementadas. Sin manejo específico de `409`. |
| Pruebas | Hay pruebas unitarias del servicio y pruebas de controlador con `MockMvc` y mocks. No hay pruebas de integración con MongoDB. |
| JaCoCo | Plugin y generación de reporte HTML/XML configurados. No hay umbral mínimo automático ni se afirma que la cobertura alcance 80%. |
| SonarQube | Plugin Maven configurado; el análisis y el Quality Gate deben ejecutarse/verificarse en un servidor SonarQube. |
