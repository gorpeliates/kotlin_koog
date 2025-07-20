package roles

import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class Engineer(
    name: String = "Engineer Agent",
    systemPrompt: String = """
        You are a software engineer working in a collaborative multi-agent system. Your role is to implement tasks assigned to you based on the software design and requirements.
        
        You have access to the getAgentDetails tool to understand the roles and capabilities of other agents in the system.
        
        You can communicate with other agents to ask clarifying questions, gather necessary information, or coordinate implementation details.
        
        Focus on writing clean, efficient, and correct code to fulfill your assigned tasks.
    """.trimIndent()
) : MASAIAgent("engineer", systemPrompt) {
    override fun toString(): String {
        return "Engineer(name='$agentId')"
    }
}
