## Camel Playground
![Java Maven CI](https://github.com/jssaggu/camel-tutorial/actions/workflows/maven.yml/badge.svg)

This project contains several files to test Camel components.

Components can be enabled/disabled through `application.yml` file.

### Videos
All the tutorial videos are available on Saggu.uk YouTube channel.

[![Watch the video](docs/Apache-Camel-Playlist.png)](https://www.youtube.com/playlist?list=PLYwGWvgqiQCnRUzcdP1h6l-d9fRjP-Ed7)

## Metrics

Camel Metrics are exposed using Spring Actuator, Prometheus, Grafana

#### To start all-in-one to test Metrics
```shell
mvn clean install -DskipTests
docker build -t saggu/camel .

cd src/main/resources/docker

docker-compose -f metrics-docker-compose.yml up -d
```

### To Access
|Application|URL|
|---|---|
|Saggu Camel|http://localhost:8080/actuator/prometheus|
|Prometheus|http://localhost:9090/|
|Grafana (admin/admin)|http://localhost:3000/|

# Open Telemetry

## Start Collector
`docker pull otel/opentelemetry-collector`

`docker run otel/opentelemetry-collector`

Or

`docker compose up -d otel`

## Start Camel Application

`java -javaagent:/Users/jasvinder.saggu/projects/downloads/opentelemetry/opentelemetry-javaagent.jar -Dotel.traces.exporter=jaeger -jar target/camel-tutorial.jar`

## Access Trace UI
http://localhost:16686/

## TLS Support
To enable TLS, you need to set the following properties in your `application.yml` file:

```yaml
spring:
  rabbitmq:
    ssl:
      enabled: true
      trust-store: classpath:docker/rabbitmq/certs/keystore.p12
      trust-store-password: changeit
      trust-store-type: PKCS12
```

### Use below commands to generate RabbitMQ certificates

```shell
mkdir -p certs && cd certs
# CA key and certificate
openssl genrsa -out ca.key 2048
openssl req -x509 -new -nodes -key ca.key -sha256 -days 1024 -out ca.crt -subj "/CN=MyCA"

# Server key and certificate signing request
openssl genrsa -out server.key 2048
openssl req -new -key server.key -out server.csr -subj "/CN=localhost"

# Sign the server cert with the CA
openssl x509 -req -in server.csr -CA ca.crt -CAkey ca.key -CAcreateserial \
-out server.crt -days 365 -sha256
```

#### Trust Store
```sh
keytool -import -alias rabbitmq -file ca.crt -keystore keystore.p12 -storetype PKCS12 -storepass changeit
```