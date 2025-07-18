package roles

import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.uuid.ExperimentalUuidApi

@Service
@OptIn(ExperimentalUuidApi::class)
class Architect @Autowired constructor(@Autowired override val executor: SingleLLMPromptExecutor) : MASAIAgent("Architect Agent",
"You are an architect. Your mission is to design a software given the requirements" +
"You can access what each other agent does by using the getAgentDetails tool." +
"You can send messages to these agents to design the software, if you need additional information to do so.") {

    override fun toString(): String {
        return "Architect(name='$name')"
    }

}
