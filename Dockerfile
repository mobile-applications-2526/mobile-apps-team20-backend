FROM openjdk:22-jdk
COPY target/CampusConnect.jar CampusConnect.jar
ENTRYPOINT ["java", "-jar", "/CampusConnect.jar"]
EXPOSE 8081