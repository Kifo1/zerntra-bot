FROM ubuntu:24.04

LABEL authors="kifo"
LABEL description="Zerntra Discord Music Bot"

# Manual java 25 installation as long as it's not available by default
RUN apt-get update && apt-get install -y \
    curl \
    ca-certificates \
    && curl -L https://download.oracle.com/java/25/latest/jdk-25_linux-aarch64_bin.tar.gz | tar -xz -C /usr/local \
    && mv /usr/local/jdk-25* /usr/local/jdk-25 \
    && rm -rf /var/lib/apt/lists/* \
    && useradd -m botuser
ENV JAVA_HOME=/usr/local/jdk-25
ENV PATH="${JAVA_HOME}/bin:${PATH}"

USER botuser
WORKDIR /home/botuser/app

COPY --chown=botuser:botuser build/libs/*-all.jar app.jar

ENV JAVA_OPTS="--enable-native-access=ALL-UNNAMED -XX:+UseZGC -XX:+ZGenerational"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar production"]