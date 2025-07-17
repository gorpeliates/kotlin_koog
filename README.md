# Kotlin Koog Project

1. Start the Jaeger service using Docker Compose:
   ```bash
   docker compose up
   ```

2. Run the server application using Gradle:
   ```bash
   ./gradlew bootRun
   ```

### Viewing Traces

Once the application is running, you can view the traces in the Jaeger UI at 
the following link as a json file:

[http://localhost:16686/api/traces?service=koog_otel_mas](http://localhost:16686/api/traces?service=koog_otel_mas)
