package roles

import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class ProductManager(name: String = "ProductManager Agent",
                     systemPrompt: String = "You are a product manager. You have two main goals: 1) to prepare a document of requirements" +
                             " for the product, 2) to make market research. "
                             + "You can access what each other agent does by using the getAgentDetails tool." +
                             "You can ask questions to these agents to prepare the requirements, if you need additional information to do so."
) : MASAIAgent(name, systemPrompt) {
    override fun toString(): String {
        return "ProductManager(name='$name')"
    }

}
