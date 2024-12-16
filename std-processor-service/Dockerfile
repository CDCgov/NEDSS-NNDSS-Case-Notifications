FROM amazoncorretto:21 as builder
WORKDIR /usr/src/stdprocessorservice
#Copy project config
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .


#Copy sources
COPY std-processor-service std-processor-service

COPY . /usr/src/stdprocessorservice

WORKDIR /usr/src/stdprocessorservice

#Build report service along with any required libraries
RUN ./gradlew :std-processor-service:buildNeeded -x test --no-daemon

FROM amazoncorretto:21

COPY --from=builder /usr/src/stdprocessorservice/std-processor-service/build/libs/std-processor-service*.jar std-processor-service.jar

# Run jar
ENV JAVA_XMS=1024m
ENV JAVA_XMX=2048m

ENTRYPOINT ["sh", "-c", "java -Xms$JAVA_XMS -Xmx$JAVA_XMX -jar std-processor-service.jar"]
