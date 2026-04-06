# 1. Use the official Temurin JDK 21 image
FROM eclipse-temurin:21-jdk AS build

# 2. Set the working directory
WORKDIR /app

# 3. Copy only the wrapper files first to leverage Docker caching
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw

# 4. Prefetch dependencies (Optional but speeds up subsequent builds)
RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -B

# 5. Copy the rest of the source code
COPY src ./src

# 6. Build the application
# We use 'package' instead of 'install' if we aren't sharing this JAR with other local projects
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean package -DskipTests -B

# 7. Final Stage: Use a slim JRE for the actual runtime (reduces image size)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/nbsback-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]