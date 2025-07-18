package roles

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.dsl.builder.forwardTo
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.nodeExecuteMultipleTools
import ai.koog.agents.core.dsl.extension.nodeLLMCompressHistory
import ai.koog.agents.core.dsl.extension.nodeLLMRequestMultiple
import ai.koog.agents.core.dsl.extension.nodeLLMSendMultipleToolResults
import ai.koog.agents.core.dsl.extension.onAssistantMessage
import ai.koog.agents.core.dsl.extension.onMultipleToolCalls
import ai.koog.agents.core.environment.ReceivedToolResult
import ai.koog.agents.core.tools.Tool
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.ToolRegistry.Companion.invoke
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
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.runBlocking
import tools.AgentCommunicationTools
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
abstract class MASAIAgent (val name: String, val systemPrompt : String){
    private val MAX_TOKENS_THRESHOLD = 1000


    //    val executor: PromptExecutor = simpleOpenRouterExecutor(dotenv()["OPEN_ROUTER_API_KEY"])
    val executor: PromptExecutor = simpleOllamaAIExecutor(dotenv()["OLLAMA_HOST"])
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

        edge(
            (nodeCallLLM forwardTo nodeFinish)
                    transformed { it.first() }
                    onAssistantMessage { true }
        )

        edge(
            (nodeCallLLM forwardTo nodeExecuteToolMultiple)
                    onMultipleToolCalls { true }
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
        edge((nodeSendToolResultMultiple forwardTo nodeExecuteToolMultiple)
                onMultipleToolCalls  {true}
        )

        edge(
            (nodeSendToolResultMultiple forwardTo nodeFinish)
                    onAssistantMessage { true }
        )
    }

    //    val aiAgentConfig = AIAgentConfig(
//        prompt = prompt("test", LLMParams(temperature = 0.0)) {
//            system(systemPrompt)
//        },
//        model = OllamaModels.Meta.LLAMA_3_2_3B,
//        maxAgentIterations = 50
//    )
    val aiAgentConfig = AIAgentConfig(
        prompt = prompt("test") {
            system(systemPrompt )
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
                println("ERROR")
                println(throwable.cause.toString())
                println(throwable.message.toString())
                println(throwable.stackTraceToString())
            }
            onAgentFinished {
                    strategyName: String, result: String? ->
                println("Result: $result")
            }
        }
    }

    fun runAgent(msg:String): String {

        var response = ""
        runBlocking {
            response = agent.runAndGetResult(msg).toString()
        }
        return response
    }
}