# Build stage - 빌드용 (무거워도 됨)
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy gradle files for dependency caching
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

# Download dependencies (will be cached if dependencies don't change)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src src

# Build the application
RUN ./gradlew bootJar --no-daemon

# Runtime stage - 실행용 (경량화)
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy only the built jar from builder stage
COPY --from=builder /app/build/libs/studybot-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Set environment variables for runtime
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV TZ=Asia/Seoul

# Run the application
CMD ["sh", "-c", "java $JAVA_OPTS -Duser.timezone=Asia/Seoul -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar app.jar"]