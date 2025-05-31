FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0

WORKDIR /app
COPY . .

# Build backend only
RUN sbt backend/compile

EXPOSE 8080
CMD ["sbt", "backend/run"] 