package roles

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.nodeExecuteSingleTool
import ai.koog.agents.core.dsl.extension.nodeExecuteTool
import ai.koog.agents.core.dsl.extension.nodeLLMRequest
import ai.koog.agents.core.dsl.extension.nodeLLMRequestMultiple
import ai.koog.agents.core.dsl.extension.nodeLLMSendToolResult
import ai.koog.agents.core.dsl.extension.onAssistantMessage
import ai.koog.agents.core.dsl.extension.onMultipleToolCalls
import ai.koog.agents.core.dsl.extension.onToolCall
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.agents.ext.tool.AskUser
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.agents.features.eventHandler.feature.handleEvents
import ai.koog.agents.features.opentelemetry.feature.OpenTelemetry
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOllamaAIExecutor
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import ai.koog.prompt.executor.llms.all.simpleOpenRouterExecutor
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import io.github.cdimascio.dotenv.dotenv
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter
import io.opentelemetry.sdk.trace.samplers.Sampler
import kotlinx.coroutines.runBlocking
import tools.AgentCommunicationTools
import kotlin.reflect.typeOf
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
abstract class MASAIAgent (val agentId: String, val systemPrompt : String) {

    val executor : PromptExecutor = simpleOpenAIExecutor(dotenv()["OPENAI_API_KEY"])

    val aiAgentConfig = AIAgentConfig(
        prompt = prompt("test") {
            system(systemPrompt)
        },
        model = LLModel(
            provider = LLMProvider.OpenAI,
            id = "gpt-4o-mini-2024-07-18",
            capabilities = listOf(
                LLMCapability.Completion, LLMCapability.Tools, LLMCapability.Embed,
                LLMCapability.PromptCaching
            )
        ),
        maxAgentIterations = 10
    )

    val toolRegistry = ToolRegistry {
        // Special tool, required with this type of agent.
        tool(AskUser)
        tool(SayToUser)
        tools(AgentCommunicationTools(agentId).asTools())
    }

    val strategy = strategy("SimpleStrategy") {

        val nodeCallLLM by nodeLLMRequest()
        val executeToolCall by nodeExecuteTool()
        val sendToolResult by nodeLLMSendToolResult()

        edge(nodeStart forwardTo nodeCallLLM)
        edge(nodeCallLLM forwardTo nodeFinish onAssistantMessage { true })
        edge(nodeCallLLM forwardTo executeToolCall onToolCall { true })
        edge(executeToolCall forwardTo sendToolResult)
        edge(sendToolResult forwardTo nodeFinish onAssistantMessage { true })
        edge(sendToolResult forwardTo executeToolCall onToolCall { true })
    }

    val agent = AIAgent(
        inputType = typeOf<String>(),
        outputType = typeOf<String>(),
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
                setVerbose(true)
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