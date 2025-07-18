package roles

import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class Engineer(
    name: String = "Engineer Agent",
    systemPrompt: String = "You are a software engineer in a software company. You will be given a task to complete. " +
            "You can access what each other agent does by using the getAgentDetails tool. " +
            "You can ask questions to these agents to write the code, if you need additional information to do so."
) : MASAIAgent(name, systemPrompt) {
    override fun toString(): String {
        return "Engineer(name='$name')"
    }
}