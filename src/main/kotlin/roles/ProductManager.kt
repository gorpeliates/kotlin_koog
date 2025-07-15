package roles

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.*
import ai.koog.agents.core.environment.ReceivedToolResult
import ai.koog.agents.core.tools.Tool
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.asTools
import ai.koog.agents.ext.tool.AskUser
import ai.koog.agents.ext.tool.SayToUser
import ai.koog.agents.features.eventHandler.feature.handleEvents
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.llms.all.simpleOpenRouterExecutor
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import io.github.cdimascio.dotenv.dotenv
import tools.AgentCommunicationTools
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ProductManager(
    val name: String,
    val port: Int,
    val systemPrompt : String = "You are a product manager. You have two main goals: 1) to prepare a document of requirements" +
        " for the product, 2) to make market research. "
) {


    override fun toString(): String {
        return "ProductManager(name='$name')"
    }



    val executor: PromptExecutor = simpleOpenRouterExecutor(dotenv()["OPEN_ROUTER_API_KEY"])

    val toolRegistry = ToolRegistry {
        // Special tool, required with this type of agent.
        tool(AskUser)
        tool(SayToUser)
        tools(AgentCommunicationTools().asTools())
    }

    val strategy = strategy("test") {
        val nodeCallLLM by nodeLLMRequestMultiple()
        val nodeExecuteToolMultiple by nodeExecuteMultipleTools(parallelTools = true)
        val nodeSendToolResultMultiple by nodeLLMSendMultipleToolResults()
        val nodeCompressHistory by nodeLLMCompressHistory<List<ReceivedToolResult>>()

        edge(nodeStart forwardTo nodeCallLLM)

        edge((nodeCallLLM forwardTo nodeFinish)
                transformed {it.first()}
                onAssistantMessage{true}
        )

        edge((nodeCallLLM forwardTo nodeExecuteToolMultiple)
                onMultipleToolCalls  {true}
        )

        edge((nodeExecuteToolMultiple forwardTo nodeCompressHistory )
                onCondition { llm.readSession { prompt.latestTokenUsage > 1000 } }
        )

        edge(nodeCompressHistory forwardTo nodeSendToolResultMultiple)

        edge((nodeExecuteToolMultiple forwardTo nodeSendToolResultMultiple )
                onCondition { llm.readSession { prompt.latestTokenUsage <= 1000 } }
        )

        edge((nodeSendToolResultMultiple forwardTo nodeExecuteToolMultiple)
                onMultipleToolCalls  {true}
        )

        edge(
            (nodeSendToolResultMultiple forwardTo nodeFinish)
                    transformed { it.first() }
                    onAssistantMessage { true }
        )
    }


    val aiAgentConfig = AIAgentConfig(
        prompt = prompt("test") {
            system(systemPrompt )
        },
        model = LLModel(
            provider = LLMProvider.OpenRouter,
            id = "deepseek/deepseek-r1-0528:free",
            capabilities = listOf(
                LLMCapability.Completion, LLMCapability.Tools, LLMCapability.Vision, LLMCapability.Embed,
                LLMCapability.PromptCaching) as List<LLMCapability>
        ),
        maxAgentIterations = 10
    )

    val agent = AIAgent(
        promptExecutor = executor,
        strategy = strategy,
        agentConfig = aiAgentConfig,
        toolRegistry = toolRegistry
    ) {
        handleEvents {
            onToolCall{
                    tool : Tool<*, *>, toolArgs : Tool.Args ->
                println("Tool called: ${tool.name} with args: $toolArgs")
            }
            onAgentRunError {
                    strategyName: String, sessionUuid: Uuid?, throwable: Throwable ->
                println("Error in strategy $strategyName with session $sessionUuid: ${throwable.message}")
            }
            onAgentFinished {
                    strategyName: String, result: String? ->
                println("Result: $result")
            }
        }
    }
}