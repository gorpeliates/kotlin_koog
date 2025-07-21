# koog-opentelemetry-mas

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
the endpoint:

### Logs

You can also see the detailed console logs of the application in the terminal, and in the `app.log` file located in the `logs` directory.

[http://localhost:16686/](http://localhost:16686/api/traces?service=koog_otel_mas)

### Task Generation

You can generate tasks for the multi-agent system using the `task_generation.ipynb` notebook. We currently use openrouter to reduce costs, can use any llm that litellm supports.

If you want to manually test the multi-agent system, send a post request to http://localhost:8080/startworkflow endpoint with the following JSON body as an example:

```json
{
   
"sender": "exampleusername",
  "task": "taskdescription"
}
```
