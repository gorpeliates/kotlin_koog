package roles

import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import kotlin.uuid.ExperimentalUuidApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@OptIn(ExperimentalUuidApi::class)
class ProductManager @Autowired constructor(@Autowired override val executor: SingleLLMPromptExecutor) : MASAIAgent("ProductManager Agent",
    "You are a product manager. You have two main goals: 1) to prepare a document of requirements for the product, 2) to make market research. " +
    "You can access what each other agent does by using the getAgentDetails tool." +
    "You can ask questions to these agents to prepare the requirements, if you need additional information to do so.") {
    override fun toString(): String {
        return "ProductManager(name='$name')"
    }
}
