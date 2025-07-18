package roles

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.nodeExecuteTool
import ai.koog.agents.core.dsl.extension.nodeLLMCompressHistory
import ai.koog.agents.core.dsl.extension.nodeLLMRequest
import ai.koog.agents.core.dsl.extension.nodeLLMSendToolResult
import ai.koog.agents.core.dsl.extension.onAssistantMessage
import ai.koog.agents.core.dsl.extension.onToolCall
import ai.koog.agents.core.environment.ReceivedToolResult
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.agents.ext.tool.AskUser
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.agents.features.eventHandler.feature.handleEvents
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.structure.json.JsonSchemaGenerator
import ai.koog.prompt.structure.json.JsonStructuredData
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import tools.AgentCommunicationTools
import kotlin.reflect.typeOf
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
abstract class MASAIAgent (val name: String, val systemPrompt : String) {
    private val MAX_TOKENS_THRESHOLD = 1000


    @Serializable
    @SerialName("AgentResponse")
    sealed class AgentResponse {
        abstract val message: String
        abstract val statusCode: Int

        @Serializable
        @SerialName("FinalMessage")
        data class FinalMessage(
            override val message: String,
            override val statusCode: Int,
        ) : AgentResponse()

        @Serializable
        @SerialName("Message")
        data class Message(
            override val message: String,
            override val statusCode: Int,
            @property:LLMDescription("The agent who receives the message")
            val receiver: String
        ) : AgentResponse()
    }

    val exampleResponses = listOf(
        AgentResponse.FinalMessage(
            message = "Other agents are not available at the moment. Please try again later.",
            statusCode = 404,
        ),
        AgentResponse.Message(
            message = "Your task is to build a calculator class for this software. Use python as the programming language. to write the code.",
            statusCode = 200,
            receiver = "Engineer Agent"
        )
    )

    val agentResponseStructure = JsonStructuredData.createJsonStructure<AgentResponse>(
        schemaFormat = JsonSchemaGenerator.SchemaFormat.JsonSchema,
        examples = exampleResponses,
        schemaType = JsonStructuredData.JsonSchemaType.SIMPLE
    )

    //    val executor: PromptExecutor = simpleOpenRouterExecutor(dotenv()["OPEN_ROUTER_API_KEY"])
    val executor: PromptExecutor = simpleOllamaAIExecutor(dotenv()["OLLAMA_HOST"])
    //
//    val structuredResponse = executor.executeStructured(
//        // Define the prompt (both system and user messages)
//        prompt = prompt("structured-data") {
//            system(
//                """
//                You are a weather forecasting assistant.
//                When asked for a weather forecast, provide a realistic but fictional forecast.
//                """.trimIndent()
//            )
//            user(
//                "What is the weather forecast for Amsterdam?"
//            )
//        },
//        // Provide the expected data structure to the LLM
//        structure = agentResponseStructure,
//        // Define the main model that will execute the request
//        mainModel = LLModel(
//            provider = LLMProvider.Ollama,
//            id = "llama3.2:3b",
//            capabilities = listOf(
//                LLMCapability.Completion, LLMCapability.Tools, LLMCapability.Embed,
//                LLMCapability.PromptCaching
//            )
//        ),
//        // Set the maximum number of retries to get a proper structured response
//        retries = 5,
//        // Set the LLM used for output coercion (transformation of malformed outputs)
//        fixingModel = LLModel(
//            provider = LLMProvider.Ollama,
//            id = "llama3.2:3b",
//            capabilities = listOf(
//                LLMCapability.Completion, LLMCapability.Tools, LLMCapability.Embed,
//                LLMCapability.PromptCaching
//            )
//        )
//    )
    val toolRegistry = ToolRegistry {
        // Special tool, required with this type of agent.
        tool(AskUser)
        tool(SayToUser)
        tools(AgentCommunicationTools().asTools())
    }

    val strategy = strategy("test") {
        val nodeCallLLM by nodeLLMRequest()
        val nodeExecuteToolMultiple by nodeExecuteTool()
        val nodeSendToolResultMultiple by nodeLLMSendToolResult()
        val nodeCompressHistory by nodeLLMCompressHistory<ReceivedToolResult>()
        val nodeStructuredData by node<String, String> { _ ->
            val structuredResponse = llm.writeSession {
                this.requestLLMStructured(
                    structure = agentResponseStructure,
                    fixingModel = LLModel(
                        provider = LLMProvider.Ollama,
                        id = "llama3.2:3b",
                        capabilities = listOf(
                            LLMCapability.Completion, LLMCapability.Tools, LLMCapability.Embed,
                            LLMCapability.PromptCaching
                        )
                    )
                )
            }

            """
            Response structure:
            $structuredResponse
            """.trimIndent()
        }
        edge(nodeStart forwardTo nodeCallLLM)

        edge(
            (nodeCallLLM forwardTo nodeStructuredData)
                    onAssistantMessage { true }
        )

        edge(
            (nodeCallLLM forwardTo nodeExecuteToolMultiple)
                    onToolCall { true }
        )

        edge(
            (nodeExecuteToolMultiple forwardTo nodeCompressHistory)
                    onCondition { llm.readSession { prompt.latestTokenUsage > MAX_TOKENS_THRESHOLD } }
        )

        edge(nodeCompressHistory forwardTo nodeSendToolResultMultiple)

        edge(
            (nodeExecuteToolMultiple forwardTo nodeSendToolResultMultiple)
                    onCondition { llm.readSession { prompt.latestTokenUsage <= MAX_TOKENS_THRESHOLD } }
        )
        edge(
            (nodeSendToolResultMultiple forwardTo nodeExecuteToolMultiple)
                    onToolCall { true }
        )

        edge(
            (nodeSendToolResultMultiple forwardTo nodeStructuredData)
                    onAssistantMessage { true }
        )

        edge(nodeStructuredData forwardTo nodeFinish)
    }


    val aiAgentConfig = AIAgentConfig(
        prompt = prompt("test") {
            system(systemPrompt)
        },
        model = LLModel(
            provider = LLMProvider.Ollama,
            id = "llama3.2:3b",
            capabilities = listOf(
                LLMCapability.Completion, LLMCapability.Tools, LLMCapability.Embed,
                LLMCapability.PromptCaching
            )
        ),
        maxAgentIterations = 10
    )

    val agent = AIAgent(
        inputType = typeOf<String>(),
        outputType = typeOf<AgentResponse>(),
        promptExecutor = executor,
        strategy = strategy,
        agentConfig = aiAgentConfig,
        toolRegistry = toolRegistry,
        installFeatures = {
            install(OpenTelemetry) {
                setServiceInfo("koog-mas-agent", "1.0.0")
                setSampler(Sampler.alwaysOn())
                addSpanExporter(
                    OtlpHttpSpanExporter.builder()
                        .setEndpoint("http://localhost:4318/v1/traces")
                        .build()
                )
            }
        }
    )


    fun runAgent(msg: String): String {

        var response = ""
        runBlocking {
            response = agent.run(msg).toString()
        }
        return response
    }
}