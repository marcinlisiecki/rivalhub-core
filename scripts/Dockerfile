FROM maven:3.6.3-openjdk-17-slim AS MAVEN_BUILD
COPY ./pom.xml ./pom.xml
RUN mvn dependency:go-offline -B
COPY ./src ./src
RUN mvn -f ./pom.xml clean package -DskipTests

FROM openjdk:17.0.2-slim-buster
EXPOSE 8080
COPY --from=MAVEN_BUILD /target/RivalHub-0.0.1-SNAPSHOT.jar /usr/local/lib/app.jar
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]