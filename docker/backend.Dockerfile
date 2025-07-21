FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0

WORKDIR /app
COPY . .

# Build backend only
RUN sbt backend/compile

# Environment variables with defaults
ENV BACKEND_PORT=8080
ENV BOT_SEARCH_DEPTH=4

EXPOSE $BACKEND_PORT
CMD ["sbt", "backend/run"] 