FROM amazoncorretto:17-alpine3.16

ARG JAR_FILE=target/*.jar
ARG APP_DIR="/opt/pokedex"
ARG APP_JAR="app.jar"

# JVM args di default
ARG APP_VM_ARGS="-XX:MaxRAMPercentage=80.0"

ENV APP_DIR=$APP_DIR
ENV APP_JAR=$APP_JAR
ENV APP_VM_ARGS=$APP_VM_ARGS
ENV TZ=Europe/Rome

EXPOSE 8080
USER root

# Timezone
RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/Europe/Rome /etc/localtime \
    && echo "Europe/Rome" > /etc/timezone \
    && apk del tzdata

# Font utili se un domani servono librerie che renderizzano testi/PDF
RUN apk add --no-cache fontconfig ttf-dejavu

RUN mkdir -p $APP_DIR
COPY ${JAR_FILE} $APP_DIR/$APP_JAR
COPY entrypoint.sh $APP_DIR/entrypoint.sh

WORKDIR $APP_DIR
RUN chmod +x ./entrypoint.sh

ENTRYPOINT ["./entrypoint.sh"]