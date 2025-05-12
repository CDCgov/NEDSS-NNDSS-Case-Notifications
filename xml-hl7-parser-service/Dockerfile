FROM amazoncorretto:21 as builder
WORKDIR /usr/src/xmlhl7parserservice
#Copy project config
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .


#Copy sources
COPY xml-hl7-parser-service xml-hl7-parser-service

COPY . /usr/src/xmlhl7parserservice

WORKDIR /usr/src/xmlhl7parserservice

#Build report service along with any required libraries
RUN ./gradlew :xml-hl7-parser-service:buildNeeded -x test --no-daemon

FROM amazoncorretto:21

COPY --from=builder /usr/src/xmlhl7parserservice/xml-hl7-parser-service/build/libs/xml-hl7-parser-service*.jar xml-hl7-parser-service.jar

# Run jar
ENV JAVA_XMS=1024m
ENV JAVA_XMX=2048m

ENTRYPOINT ["sh", "-c", "java -Xms$JAVA_XMS -Xmx$JAVA_XMX -jar xml-hl7-parser-service.jar"]
