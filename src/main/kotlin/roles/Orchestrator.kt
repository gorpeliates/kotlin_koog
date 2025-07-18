package roles

import kotlinx.coroutines.runBlocking
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class Orchestrator(name : String = "Company Agent",
                   systemPrompt : String = "You are a software company. You should communicate with the other agents in the company to create the workflow for building a software."
                      + "You can access what each other agent does by using the getAgentDetails tool."
) : MASAIAgent(name, systemPrompt) {

    override fun toString(): String {
        return "Orchestrator(name='$agentId')"
    }
    /**
     * Starts workflow with the given task and returns a string as a result if succesful
     */
    fun startTask(task: String) : String{
        return runBlocking {
            agent.run(task).toString()
        }
    }
}