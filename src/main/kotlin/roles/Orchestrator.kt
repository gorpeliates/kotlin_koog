package roles

import kotlinx.coroutines.runBlocking
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class Orchestrator(
    name: String = "Company Agent",
    systemPrompt: String = """
        You represent a software company responsible for orchestrating the collaboration between agents to complete software development tasks.
        
        Your role is to initiate workflows, coordinate tasks between agents (such as architects and engineers), and ensure the smooth execution of the project.
        
        You have access to the getAgentDetails tool to inspect each agent's role and capabilities.
        
        Communicate with the relevant agents to delegate tasks, gather updates, and manage dependencies effectively.
    """.trimIndent()
) : MASAIAgent(name, systemPrompt) {

    override fun toString(): String {
        return "Orchestrator(name='$agentId')"
    }

    /**
     * Starts workflow with the given task and returns a string as a result if successful
     */
    fun startTask(task: String): String {
        return runBlocking {
            agent.run(task).toString()
        }
    }
}
