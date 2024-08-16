# Stage 1: Build the application
FROM maven:3.9.8-amazoncorretto-17-debian AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-oracle
WORKDIR /app
COPY --from=build /app/target/library-0.0.1-SNAPSHOT.jar /app/library-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/library-0.0.1-SNAPSHOT.jar"]
