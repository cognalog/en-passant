# Build stage
FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0 as builder

WORKDIR /app
COPY . .

# Build frontend only
RUN sbt frontend/fastLinkJS

# Runtime stage
FROM nginx:alpine

# Copy the built frontend files
COPY --from=builder /app/frontend/target/scala-2.13/en-passant-frontend-fastopt/main.js /usr/share/nginx/html/
COPY --from=builder /app/frontend/src/main/resources/* /usr/share/nginx/html/

# Copy nginx configuration
COPY docker/nginx.conf /etc/nginx/conf.d/default.conf 