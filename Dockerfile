FROM openjdk:22-jdk
ADD target/CampusConnect.jar CampusConnect.jar
ENTRYPOINT ["java", "-jar", "/ToDoApp.jar"]
EXPOSE 8081