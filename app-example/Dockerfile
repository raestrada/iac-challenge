FROM openjdk:17-slim as builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

# Environment variables for the application
ENV SERVER_PORT=8080
ENV SPRING_PROFILES_ACTIVE=default

# Default memory settings - overridable via container resource limits
ENV JAVA_XMS="256m"
ENV JAVA_XMX="512m"

# Kubernetes optimization flags
ENV JAVA_OPTS="\
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -XX:+ExitOnOutOfMemoryError \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/tmp/heapdump.hprof \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -XX:GCTimeRatio=4 \
    -XX:+ParallelRefProcEnabled \
    -XX:+DisableExplicitGC \
    -Djava.security.egd=file:/dev/./urandom \
    -Duser.timezone=UTC \
    -Dfile.encoding=UTF-8"

EXPOSE 8080

# Use exec form with shell to expand environment variables
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Xms${JAVA_XMS} -Xmx${JAVA_XMX} -jar /app/app.jar"]
