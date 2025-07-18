package roles

import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import kotlin.uuid.ExperimentalUuidApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@OptIn(ExperimentalUuidApi::class)
class Engineer @Autowired constructor(@Autowired override val executor: SingleLLMPromptExecutor) : MASAIAgent("Engineer Agent",
    "You are a software engineer in a software company. You will be given a task to complete. " +
    "You can access what each other agent does by using the getAgentDetails tool. " +
    "You can ask questions to these agents to write the code, if you need additional information to do so.") {
    override fun toString(): String {
        return "Engineer(name='$name')"
    }
}