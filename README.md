# amazon-s3-quickstart Project

This project is a demonstration of how to interact with S3 in Quarkus.
It uses LocalStack as a S3 mock server.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

You can also connect to http://localhost:8080/s3.html

### Dev Services

In dev mode, the app will automatically raise a fully configured MinIO container with an initial bucket:
>quarkus.s3.quickstart

You need a running _docker daemon_ to make it work.

It can be disabled by setting this property in _application.yaml_:
> %dev.quarkus.s3.enabled: _false_

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.