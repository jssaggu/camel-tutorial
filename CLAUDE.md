# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

`README.md` is the user-facing entry point and has the full build / run / Docker instructions;
this file is the architecture-oriented companion.

## What this is

A single Spring Boot app (`com.jss.CamelApplication`) that acts as a playground of ~40 independent Apache Camel routes, each demonstrating one integration pattern or component (EIPs, error handling, JMS/ActiveMQ, RabbitMQ, Kafka, NATS, SAGA, circuit breaker, REST DSL, metrics, OpenTelemetry). Tutorial videos: https://www.youtube.com/playlist?list=PLYwGWvgqiQCnRUzcdP1h6l-d9fRjP-Ed7

- Spring Boot 4.1.x, Camel 4.22.x (LTS), Java 21, Maven (no wrapper — use system `mvn`). Build needs a JDK 21.
- Packaged as `target/camel-tutorial.jar`. Also war-deployable (`SpringBootServletInitializer`, `provided` Tomcat).
- Spring Boot 4 / Framework 7 baseline: Jakarta EE 11, **Jackson 3** (`tools.jackson.*`; `com.fasterxml.jackson.annotation` annotations unchanged), JUnit 6, health SPI moved to `org.springframework.boot.health.contributor.*`, test helpers repackaged (`TestRestTemplate` → `org.springframework.boot.resttestclient`, needs `@AutoConfigureTestRestTemplate` + `spring-boot-restclient` on the test classpath).

## Commands

```bash
mvn package                       # build + run tests (this is what CI runs: mvn -B package)
mvn clean install -DskipTests     # build without tests
mvn test -Dtest='!WeatherRouteTestcontainersTest'  # skip the Docker-dependent test
mvn spring-boot:run               # run the app on :8080
java -jar target/camel-tutorial.jar

mvn test -Dtest=ChoiceRouteTest             # single test class
mvn test -Dtest=ChoiceRouteTest#methodName  # single test method

mvn spotless:apply                # auto-format; REQUIRED before build will pass on changed files
```

### Formatting is enforced by the build

Spotless (`google-java-format`, **AOSP** style, plugin version pinned, GJF version left at the plugin default) runs `check` during the `compile` phase with `ratchetFrom=origin/main`, so any `.java` file you changed relative to `origin/main` must be formatted or the build fails. Run `mvn spotless:apply` after editing — it reformats (and removes unused imports from) only the files you changed. The `// spotless:off` / `// spotless:on` toggle is available.

A second Spotless format covers `*.md` / `.gitignore` (trailing-whitespace + final-newline only — the tab-indent step was removed because it corrupted fenced code blocks). Editing docs is safe.

## How routes are enabled/disabled — the central mechanism

Nothing runs by default except RabbitMQ. Every route `@Component` and most `@Configuration` classes are gated by `@ConditionalOnProperty` (or `@ConditionalOnExpression`) on a `jss.camel.<feature>.enabled` flag defined in `src/main/resources/application.yml`. That file is the control panel.

- To work on a route, flip its flag to `true` in `application.yml` (or pass `--jss.camel.<feature>.enabled=true` / `-Djss.camel.<feature>.enabled=true`).
- Tests enable what they need via `@SpringBootTest(properties = {"jss.camel.rabbitmq.enabled=true", ...})` or by constructing the `RouteBuilder` directly. `src/test/resources/application.properties` disables the RabbitMQ routes by default so Spring-context tests stay hermetic.
- `application.yml` also carries a note: some features (JMS/ActiveMQ) additionally require editing `spring.autoconfigure.exclude` (Spring Boot 4 FQN: `org.springframework.boot.activemq.autoconfigure.ActiveMQAutoConfiguration`).

## Code layout

- `src/main/java/com/jss/routes/**` — one package (or class) per pattern; the `RouteBuilder` subclass is the entry point. Look at the matching `@ConditionalOnProperty` name to find its flag.
- `src/main/java/com/jss/config/` , `.../routes/*/Configuration.java` — Camel/Spring beans (connection factories, metrics policy factories), also conditionally loaded.
- `src/main/java/com/jss/dto/` — Jackson/JAXB payload types.
- `src/main/java/com/jss/health/` — custom Actuator `HealthIndicator`s.
- `src/main/resources/application.yml` — feature flags, thread pools, RabbitMQ/SSL, actuator exposure, logging.
- `src/main/resources/camel-routes/routes.xml` — XML-DSL routes (currently empty; `routes-reload-pattern` is commented out in yml).
- `src/main/resources/docker/` — compose files for `metrics-docker-compose.yml` (Prometheus/Grafana), `kafka-docker-compose.yml`, `rabbit-docker-compose.yml`, `open-telemetry/`.

## REST endpoints

Camel REST uses `camel-servlet` mounted at context-path `/services/*` (see `camel.servlet.mapping` in yml); app port `8080`. Actuator exposes `health`, `info`, `prometheus`, `liveness` (health details hidden).

## RabbitMQ routes

- Use the `spring-rabbitmq` component. Shared constants (exchange/queue/routing-key names, `RABBIT_URI` template) live in `routes/rabbitmq/RabbitmqConfiguration.java`.
- `application.yml` points at `localhost:5671` with `ssl.enabled` toggles; plain broker is `5672`, management UI `15672`. Start one with `docker compose -f src/main/resources/docker/rabbit-docker-compose.yml up -d`.
- TLS: certs under `src/main/resources/docker/rabbitmq/certs/` are **gitignored** (`*.p12 *.crt *.key *.csr *.srl`). Regenerate with the `openssl` / `keytool` commands in `README.md`.

## Tests

- **JUnit 6** (via Spring Boot 4). Unit-style route tests extend `org.apache.camel.test.junit6.CamelTestSupport` and override `createRouteBuilder()`; Spring-context tests use `@CamelSpringBootTest` + `@SpringBootTest` (Camel `camel-test-junit6` / `camel-test-spring-junit6`).
- Surefire runs with `reuseForks=false` (fork per test class): `CamelTestSupport` caches a `CamelContext` per fork, and once a `@CamelSpringBootTest` closes its context the plain `CamelTestSupport` tests in the same fork would otherwise see a stopped context.
- Testcontainers **2.x**: module artifacts are prefixed `testcontainers-` (e.g. `testcontainers-junit-jupiter`), and `DockerComposeContainer` → `ComposeContainer`. `WeatherRouteTestcontainersTest` / `TestContainerLaunchConfig` spin up RabbitMQ from `src/test/resources/docker-compose.yml`; the test is `@Testcontainers(disabledWithoutDocker = true)` so it **skips automatically when Docker is unavailable**. It manually declares exchanges/queues because `camel-spring-rabbitmq` does not auto-create them.
- Two tests are `@Disabled` in source (`FileHandlerRouteTest`, `WeatherRouteTest`) — expected skips.
