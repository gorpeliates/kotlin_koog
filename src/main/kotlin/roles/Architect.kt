package roles

import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class Architect(name : String = "Architect Agent",
                systemPrompt: String = """
                    You are a software architect. Your goal is to design nsoftware systems based on the given requirements.
                    You have access to the getAgentDetails tool, which allows you to inspect the roles and responsibilities of other agents in the system.
                    You can use this tool to understand the capabilities of other agents and their identifiers for sending messages.
                    You can communicate with other agents to clarify requirements, delegate tasks, or gather additional information as eeded.
                    Focus on defining high-level components, responsibilities, and interactions between agents to create a clear and coherent system architecture.
                """.trimIndent()
) : MASAIAgent(name, systemPrompt) {

    override fun toString(): String {
        return "Architect(name='$agentId')"
    }

}
