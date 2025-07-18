package roles

import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class Architect(name : String = "Architect Agent",
                systemPrompt : String = "You are an architect. Your mission is to design a software given the requirements" +
                        "You can access what each other agent does by using the getAgentDetails tool." +
                        "You can send messages to these agents to design the software, if you need additional information to do so."
) : MASAIAgent(name, systemPrompt) {

    override fun toString(): String {
        return "Architect(name='$agentId')"
    }

}
