FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 8181

CMD ["java", "-Dloader.main=com.project.clientcarepro.ClientCareProApplication", "-jar", "target/ClientCarePro-0.0.1-SNAPSHOT.jar"]