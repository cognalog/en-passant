FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0

WORKDIR /app
COPY . .

# Build frontend and backend
RUN sbt frontend/fastLinkJS backend/compile

# Create a directory for static files
RUN mkdir -p backend/src/main/resources/web
RUN cp -r frontend/target/scala-2.13/en-passant-frontend-fastopt/* backend/src/main/resources/web/
RUN cp frontend/src/main/resources/index.html backend/src/main/resources/web/

# Environment variables with defaults
ENV BACKEND_PORT=8080
ENV BOT_SEARCH_DEPTH=4

EXPOSE $BACKEND_PORT
CMD ["sbt", "backend/run"] 