# DocumentSender
Small document sender between microservices


## Idea

Two microservices(Writer and Reader) are connected to each other via Kafka (topic documentSender)

Reader accepts documents like doc, odt and some pdf via REST endpoint(localhost:8080/sender/upload),
parses it using Apache Tika, serializes content and metadata via Avro and sends to Kafka topic.

Writer is listening for topic and reading document contents after deserealizing Avro document.


## Build and run

Reader:
```yml

gradle clean build (-x test to skipt tests)

java -jar <jar_location> Reader-1.0.jar or gradle bootRun
```

Writer:
```yml

gradle clean build

java -jar <jar_location> Writer-1.0.jar or gradle run
```

```yml

gradle bootJar
```

## Swagger ui
```
http://localhost:8020/swagger-ui/index.html
```
## Security

Check credentials in application.yaml