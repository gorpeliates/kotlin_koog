import io.opentelemetry.sdk.common.CompletableResultCode
import io.opentelemetry.sdk.trace.export.SpanExporter
import io.opentelemetry.sdk.trace.data.SpanData
import java.io.File
import java.io.FileWriter

class FileSpanExporter(file: File) : SpanExporter {
    private val writer = FileWriter(file, true)

    override fun export(spans: Collection<SpanData>): CompletableResultCode {
        try {
            for (span in spans) {
                writer.write(span.toString() + "\n")
            }
            writer.flush()
            return CompletableResultCode.ofSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            return CompletableResultCode.ofFailure()
        }
    }

    override fun flush(): CompletableResultCode {
        return CompletableResultCode.ofSuccess()
    }

    override fun shutdown(): CompletableResultCode {
        writer.close()
        return CompletableResultCode.ofSuccess()
    }
}
