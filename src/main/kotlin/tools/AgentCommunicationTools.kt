package tools

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import io.github.cdimascio.dotenv.dotenv
import org.springframework.web.client.RestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

@LLMDescription("Tools for communicating with other agents through their API endpoints.")
class AgentCommunicationTools : ToolSet{

    private val dotenv = dotenv()
    private val restTemplate = RestTemplate()

    @Tool
    @LLMDescription("Sends a message to another agent via HTTP POST request")
    fun sendMessage(
        @LLMDescription(description = "The URL endpoint for the chosen agent.")
        agentEndpoint: String,
        @LLMDescription(description = "The message to be sent to the chosen agent.")
        message: String
    ): String {
        return try {
            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
            }
            val serverURL = dotenv["SPRING_SERVER_URL"]
            val requestBody = mapOf("message" to message)
            val request = HttpEntity(requestBody, headers)

            val response = restTemplate.postForObject(serverURL+agentEndpoint, request, String::class.java )
            response ?: "No response received"
        } catch (e: Exception) {
            "Error sending message: ${e.message}"
        }
    }
}