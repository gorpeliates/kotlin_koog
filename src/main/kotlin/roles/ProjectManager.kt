package roles

import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import kotlin.uuid.ExperimentalUuidApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@OptIn(ExperimentalUuidApi::class)
class ProjectManager @Autowired constructor(@Autowired override val executor: SingleLLMPromptExecutor) : MASAIAgent("ProjectManager Agent",
    "You are a project manager. You should break down the tasks given the product design."
    + "You can access what each other agent does by using the getAgentDetails tool."
    + "You can ask questions to these agents to prepare the tasks, if you need additional information to do so.") {
    override fun toString(): String {
        return "ProjectManager(name='$name')"
    }
}
