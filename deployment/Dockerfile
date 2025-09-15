FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --chown=appuser:appgroup ../applications/app-service/build/libs/cy_applications.jar cy_applications.jar
ENV JAVA_OPTS=" -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
USER appuser
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -jar cy_applications.jar" ]
