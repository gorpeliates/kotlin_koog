package tools

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import io.github.cdimascio.dotenv.dotenv
import io.opentelemetry.api.GlobalOpenTelemetry
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.RestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

@Suppress("unused")
@LLMDescription("Tools for communicating with other agents through their API endpoints and getting their information.")
class AgentCommunicationTools(val agentId: String) : ToolSet{

    private val serverURL: String = dotenv()["SPRING_SERVER_URL"]

    private  var restTemplate : RestTemplate = RestTemplateBuilder().build()

    private val idToAgents : Set<String> = setOf(
        "engineer" ,
        "productManager" ,
        "projectManager" ,
        "architect"
    ).filter { it != agentId }.toSet()

    @Tool
    @LLMDescription("Get the details of all the agents in the server.")
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
        @LLMDescription(
            description = "The URL endpoint for the chosen agent. " +
                    "You only need to provide the agent_id part as a parameter."
        )
        agentId: String,
        @LLMDescription(description = "The message to be sent to the chosen agent.")
        message: String
    ): String {
        return try {


            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
            }

            println("Sending message with params: message: $message, agentEndpoint: $agentId")
            val requestBody = mapOf("sender" to this.name,"message" to message)
            val request = HttpEntity(requestBody, headers)

            val response = restTemplate.postForObject("$serverURL/sendmessage/$agentId", request, String::class.java )
            response ?: "No response received"
        } catch (e: Exception) {
            "Error sending message: ${e.message}"
        } finally {
            span.end()
        }
    }

    @Tool
    @LLMDescription("Random number generator tool.")
    fun generateRandomNumber(): String {

        return try {
            val randomNumber = (1..100).random().toString()
            span.setAttribute("agent.id", agentId)
            span.setAttribute("random.number", randomNumber)
            randomNumber

        } catch (e : Exception) {
            "Error generating random number: ${e.message}"
        } finally {
            span.end()
        }
    }

}