package roles

import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import ai.koog.prompt.dsl.prompt
import kotlinx.coroutines.runBlocking
import kotlin.uuid.ExperimentalUuidApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@OptIn(ExperimentalUuidApi::class)
class Orchestrator @Autowired constructor(@Autowired override val executor: SingleLLMPromptExecutor) : MASAIAgent("Company Agent",
    "You are a software company. You should communicate with the other agents in the company to create the workflow for building a software."
    + "You can access what each other agent does by using the getAgentDetails tool.") {

    override fun toString(): String {
        return "Orchestrator(name='$name')"
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