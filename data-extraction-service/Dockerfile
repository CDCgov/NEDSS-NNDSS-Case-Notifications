FROM amazoncorretto:21 as builder
WORKDIR /usr/src/dataextractionservice
#Copy project config
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .


#Copy sources
COPY data-extraction-service data-extraction-service

COPY . /usr/src/dataextractionservice

WORKDIR /usr/src/dataextractionservice

#Build report service along with any required libraries
RUN ./gradlew :data-extraction-service:buildNeeded -x test --no-daemon

FROM amazoncorretto:21

COPY --from=builder /usr/src/dataextractionservice/data-extraction-service/build/libs/data-extraction-service*.jar data-extraction-service.jar

# Run jar
ENV JAVA_XMS=1024m
ENV JAVA_XMX=2048m

ENTRYPOINT ["sh", "-c", "java -Xms$JAVA_XMS -Xmx$JAVA_XMX -jar data-extraction-service.jar"]
