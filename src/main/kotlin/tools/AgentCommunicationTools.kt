package tools

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import io.github.cdimascio.dotenv.dotenv
import org.springframework.web.client.RestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.getForEntity

@Suppress("unused")
@LLMDescription("Tools for communicating with other agents through their API endpoints.")
class AgentCommunicationTools : ToolSet{

    private val serverURL: String = dotenv()["SPRING_SERVER_URL"]
    private val restTemplate = RestTemplate()

    @Tool
    @LLMDescription("Get the details of all the agents from their agent cards.")
    fun getAgentDetails() : String{
        return try {
            val endpoint = "$serverURL/.well-known"
            val response = restTemplate.getForObject(endpoint, String::class.java)
            response
        } catch (e: Exception) {
            println("Error retrieving agent cards: ${e.message}")
        } as String
    }

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

            println("Sending message with params: message: $message, agentEndpoint: $agentEndpoint")
            val requestBody = mapOf("sender" to this.name,"message" to message)
            val request = HttpEntity(requestBody, headers)

            val response = restTemplate.postForObject("$serverURL/$agentEndpoint", request, String::class.java )
            response ?: "No response received"
        } catch (e: Exception) {
            "Error sending message: ${e.message}"
        }
    }


}