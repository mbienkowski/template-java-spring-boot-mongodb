FROM openjdk:19-alpine

ARG HOST_UID=10000

RUN apk add --update \
    curl \
    tzdata && \
    rm -rf /var/cache/apk/*

RUN cp /usr/share/zoneinfo/Europe/Warsaw /etc/localtime && \
    echo "Europe/Warsaw" > /etc/timezone

WORKDIR /usr/src/app
COPY ./build/libs/template-java-spring-boot-mongodb.jar ./app.jar

RUN adduser -H -D -s /bin/sh -u ${HOST_UID} appuser || true
USER appuser

CMD ["java", "-jar", "app.jar"]