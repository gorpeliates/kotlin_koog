import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.opentelemetry.sdk.common.CompletableResultCode
import io.opentelemetry.sdk.trace.data.SpanData
import io.opentelemetry.sdk.trace.export.SpanExporter
import java.io.File
import java.time.Instant

class JsonFileSpanExporter(private val file: File) : SpanExporter {
    private val objectMapper = ObjectMapper()
        .registerKotlinModule()
        .registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .enable(SerializationFeature.INDENT_OUTPUT)

    private val lock = Any()
    private val allSpans = mutableListOf<Map<String, Any?>>()

    init {
        // Create file if it doesn't exist
        if (!file.exists()) {
            file.createNewFile()
        }
    }

    override fun export(spans: Collection<SpanData>): CompletableResultCode {
        synchronized(lock) {
            try {
                spans.forEach { span ->
                    allSpans.add(spanToJson(span))
                }
                // Save after each export (for now)
                objectMapper.writeValue(file, allSpans)
                return CompletableResultCode.ofSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                return CompletableResultCode.ofFailure()
            }
        }
    }

    override fun flush(): CompletableResultCode = CompletableResultCode.ofSuccess()

    override fun shutdown(): CompletableResultCode = CompletableResultCode.ofSuccess()

    private fun spanToJson(span: SpanData): Map<String, Any?> {
        return mapOf(
            "traceId" to span.traceId,
            "spanId" to span.spanId,
            "parentSpanId" to span.parentSpanId,
            "name" to span.name,
            "kind" to span.kind.name,
            "startEpochNanos" to span.startEpochNanos,
            "endEpochNanos" to span.endEpochNanos,
            "startTime" to Instant.ofEpochMilli(span.startEpochNanos / 1_000_000),
            "endTime" to Instant.ofEpochMilli(span.endEpochNanos / 1_000_000),
            "attributes" to span.attributes.asMap().mapValues { it.value.toString() },
            "status" to span.status.statusCode.name,
            "events" to span.events.map {
                mapOf(
                    "name" to it.name,
                    "time" to Instant.ofEpochMilli(it.epochNanos / 1_000_000),
                    "attributes" to it.attributes.asMap().mapValues { attr -> attr.value.toString() }
                )
            }
        )
    }
}
