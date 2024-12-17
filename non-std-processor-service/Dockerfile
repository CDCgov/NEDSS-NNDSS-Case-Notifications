FROM amazoncorretto:21 as builder
WORKDIR /usr/src/nonstdprocessorservice
#Copy project config
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .


#Copy sources
COPY non-std-processor-service non-std-processor-service

COPY . /usr/src/nonstdprocessorservice

WORKDIR /usr/src/nonstdprocessorservice

#Build report service along with any required libraries
RUN ./gradlew :non-std-processor-service:buildNeeded -x test --no-daemon

FROM amazoncorretto:21

COPY --from=builder /usr/src/nonstdprocessorservice/non-std-processor-service/build/libs/non-std-processor-service*.jar non-std-processor-service.jar

# Run jar
ENV JAVA_XMS=1024m
ENV JAVA_XMX=2048m

ENTRYPOINT ["sh", "-c", "java -Xms$JAVA_XMS -Xmx$JAVA_XMX -jar non-std-processor-service.jar"]
