package server

import io.a2a.spec.AgentCard
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.trace.Span
import io.opentelemetry.context.Context
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MASController @Autowired constructor(
    private val tracer: io.opentelemetry.api.trace.Tracer,
    private val agentServer: AgentServer,
    ){

    @field:Value("\${spring.application.name}")
    private lateinit var applicationName: String

    private final val logger = LoggerFactory.getLogger(MASController::class.java)

        @PostMapping("/sendmessage/{agentId}")
    fun sendMessage(
            @PathVariable("agentId") agentId: String,
            @RequestBody body: Map<String, String>
    ): ResponseEntity<String?> {
            val currentSpan = tracer.spanBuilder("send-message").setParent(Context.current()).startSpan()

            try {
                val requestId = MDC.get("requestId") ?: "N/A"
                val logMessage = "[$requestId] [$applicationName] Sending message to agent $agentId: ${body["message"]}"

                logger.info(logMessage)
                currentSpan
                    .setAttribute("requestId", requestId)
                    .setAttribute("agentId", agentId)
                    .setAttribute("message", logMessage)

                val agent = agentServer.getAgent(agentId)
                val response : String = agent.runAgent(body["message"] as String)

                currentSpan.setAttribute("response", response)
                currentSpan.end()
                return ResponseEntity(response, HttpStatus.OK)
            } catch (e : Exception) {

                logger.error("Error processing message for agent $agentId", e)
                currentSpan.setAttribute("error", "Error during sending message to agent $agentId")
                currentSpan.setAttribute("stacktrace", e.stackTrace.joinToString("\n"))
                currentSpan.end()
                return ResponseEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
            }
    }

    @GetMapping("/.well-known")
    @ResponseBody
    fun agentCards() : ResponseEntity<List<AgentCard>> {

        val currentSpan = tracer.spanBuilder("agent-cards-request").setParent(Context.current()).startSpan()
        try {
            val response = AgentServer().getAllAgentCards()
            val requestId = MDC.get("requestId") ?: "N/A"

            logger.info("[$requestId] [$applicationName] Incoming request to /.well-known - Response: $response")
            currentSpan.setAttribute("requestId", requestId)
            currentSpan.setAttribute("response", response.toString())
            currentSpan.end()
            return ResponseEntity(response, HttpStatus.OK)

        } catch (e : Exception) {
            currentSpan.setAttribute("error", "Error during agentCards request")
            currentSpan.setAttribute("stacktrace", e.stackTrace.joinToString("\n"))
            currentSpan.end()
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PostMapping("/startworkflow")
    fun startWorkflow(@RequestBody task: String):ResponseEntity<String> {
        val currentSpan  = tracer.spanBuilder("workflow-RANDOMIZEDUUID").setParent(Context.current()).startSpan()
        currentSpan.setAttribute("task", task)

        try {
            val response = AgentServer().startWorkflow(task)
            currentSpan.setAttribute("response", response)
            return ResponseEntity(response, HttpStatus.OK)

        }catch (e : Exception){
            currentSpan.setAttribute("error", "Error during workflow")
            currentSpan.setAttribute("stacktrace", e.stackTrace.joinToString("\n"))
            currentSpan.end()
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}