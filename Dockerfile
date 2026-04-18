FROM eclipse-temurin:21-jre-noble

LABEL authors="kifo"
LABEL description="Zerntra Discord Music Bot"

RUN useradd -m botuser
USER botuser
WORKDIR /home/botuser/app

COPY --chown=botuser:botuser build/libs/*-all.jar app.jar

ENV JAVA_OPTS="--enable-native-access=ALL-UNNAMED -XX:+UseZGC -XX:+ZGenerational"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]