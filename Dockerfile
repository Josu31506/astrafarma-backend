# -------- Stage 1: Build the JAR with Maven --------
FROM maven:3.9.1-eclipse-temurin-17 AS builder
WORKDIR /app

# Copiamos solo el pom para aprovechar la cache de Maven
COPY pom.xml .
# Descargamos las dependencias sin compilar todavía
RUN mvn dependency:go-offline -B

# Ahora copiamos el código fuente y compilamos
COPY src ./src
RUN mvn clean package -DskipTests -B

# -------- Stage 2: Ejecutar la aplicación --------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Exponemos el puerto que usa Spring Boot
EXPOSE 8080

# Copiamos el JAR generado en el stage anterior
COPY --from=builder /app/target/astrafarma-backend-0.0.1-SNAPSHOT.jar app.jar

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]
