import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.llms.all.simpleOpenRouterExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import io.github.cdimascio.dotenv.dotenv
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.exporter.logging.LoggingSpanExporter
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor
import kotlinx.coroutines.runBlocking



fun initOpenTelemetry(): OpenTelemetry {

    val exporter = LoggingSpanExporter.create()

    val tracerProvider = SdkTracerProvider.builder()
        .addSpanProcessor(SimpleSpanProcessor.create(exporter))
        .build()

    val openTelemetry = OpenTelemetrySdk.builder()
        .setTracerProvider(tracerProvider)
        .build()

    return openTelemetry
}

fun main_test() {

    val openTelemetry = initOpenTelemetry()
    val tracer: Tracer = openTelemetry.getTracer("ai.agent.tracer")


    val dotenv = dotenv()

    val llmModel = LLModel(
        LLMProvider.OpenRouter,
        "deepseek/deepseek-r1:free",
        listOf(
            LLMCapability.Completion, LLMCapability.Tools, LLMCapability.Document, LLMCapability.PromptCaching
        )
    )

    val apiKey = dotenv["OPEN_ROUTER_API_KEY"]

    val agentA = AIAgent(
        executor = simpleOpenRouterExecutor(apiKey),
        llmModel = llmModel,
        systemPrompt = "You are a coding assistant. Your server.main duty is to write clean and efficient code.",
        temperature = 0.5
    )

    val prompt = "In the current working directory, create a kotlin codes snippet that sends a get request to youtube.com. Ask me for further details"

    runBlocking {
        val span = tracer.spanBuilder("Agent Interaction").startSpan()
        span.makeCurrent().use {
            val responseA = agentA.runAndGetResult(prompt)
            println("Agent A response:\n$responseA")
        }
        span.end()
    }

}