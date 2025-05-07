FROM amazoncorretto:21 as builder
WORKDIR /usr/src/casenotificationservice
#Copy project config
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .


#Copy sources
COPY case-notification-service case-notification-service

COPY . /usr/src/casenotificationservice

WORKDIR /usr/src/casenotificationservice

#Build report service along with any required libraries
RUN ./gradlew :case-notification-service:buildNeeded -x test --no-daemon

FROM amazoncorretto:21

COPY --from=builder /usr/src/casenotificationservice/case-notification-service/build/libs/case-notification-service*.jar case-notification-service.jar

# Run jar
ENV JAVA_XMS=1024m
ENV JAVA_XMX=2048m

ENTRYPOINT ["sh", "-c", "java -Xms$JAVA_XMS -Xmx$JAVA_XMX -jar case-notification-service.jar"]
