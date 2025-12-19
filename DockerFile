FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/bugetbaba-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "bugetbaba-0.0.1-SNAPSHOT.jar"]
