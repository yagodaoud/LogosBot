FROM openjdk:19
WORKDIR /app
COPY out/artifacts/discord_bot_java_jar/discord-bot-java.jar /app/discord-bot-java.jar
ENTRYPOINT ["java", "-jar", "discord-bot-java.jar"]