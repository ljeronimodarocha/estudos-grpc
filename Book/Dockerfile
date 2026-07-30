# Stage 1: Install parent POM
FROM maven:3.9.9 AS builder
WORKDIR /build
COPY . /build/
RUN mvn install -N -q

# Stage 2: Build module
FROM builder AS module-builder
WORKDIR /build
COPY . /build/
RUN mvn install -pl Book -am -DskipTests -q

# Stage 3: Create runtime image
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=module-builder /build/Book/target/book-*.jar /app/book.jar
EXPOSE 8081
CMD ["java", "-jar", "/app/book.jar"]
