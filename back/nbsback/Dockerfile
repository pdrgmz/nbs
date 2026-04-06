# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:21-jdk

# Install Maven
RUN apt-get update && apt-get install -y --no-install-recommends maven && rm -rf /var/lib/apt/lists/*

# Verify Java version
RUN java -version

# Set the working directory in the container
WORKDIR /app

# Build the application
COPY . /app/.
RUN --mount=type=cache,id=maven-repo,target=/root/.m2/repository \
    chmod +x ./mvnw && ./mvnw -DoutputFile=target/mvn-dependency-list.log -B -DskipTests clean install

# Copy the application's JAR file into the container
COPY target/nbsback-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]