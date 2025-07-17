package server

import io.a2a.spec.AgentCard
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
import org.springframework.web.client.RestTemplate

@Controller
class MASController @Autowired constructor(
    private val agentServer: AgentServer,
    private val restTemplate: RestTemplate
    ){

    @field:Value("\${spring.application.name}")
    private lateinit var applicationName: String

    private final val logger = LoggerFactory.getLogger(MASController::class.java)
        @PostMapping("/sendmessage/{agentId}")
    fun sendMessage(
            @PathVariable("agentId") agentId: String,
            @RequestBody body: Map<String, String>
    ): ResponseEntity<String?> {
            try {

                val requestId = MDC.get("requestId") ?: "N/A"
                logger.info("[$requestId] [$applicationName] Sending message to agent $agentId: ${body["message"]}")

                val agent = agentServer.getAgent(Integer.parseInt(agentId))
                val response : String = agent.runAgent(body["message"] as String)
                return ResponseEntity(response, HttpStatus.OK)
            } catch (e : Exception) {
                return ResponseEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
            }
    }

    @GetMapping("/.well-known")
    @ResponseBody
    fun agentCards() : ResponseEntity<List<AgentCard>> {
        try {
            val response = agentServer.getAllAgentCards()
            val requestId = MDC.get("requestId") ?: "N/A"
            logger.info("[$requestId] [$applicationName] Incoming request to /.well-known - Response: $response")
            return ResponseEntity(response, HttpStatus.OK)

        } catch (_ : Exception) {
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}