# amazon-s3-quickstart Project

This project is a demonstration of how to interact with S3 in Quarkus.
It uses LocalStack as a S3 mock server.

## Running LocalStack

```
docker-compose up --build -d
```

### Create a bucket
Enter in the container's terminal:
```
docker exec -it $(docker ps -f name=amazon-s3-quickstart-localstack-1 -q) sh
```
Then create a profile:
```
aws configure --profile localstack
```
Provide those data:
- test-key
- test-secret
- us-east-1

Now you can create a bucket:
```
aws s3 mb s3://quarkus.s3.quickstart --profile localstack --endpoint-url=http://localhost:4566
```

> **_NOTE:_** Quarkus is already configured to work with those data

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

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