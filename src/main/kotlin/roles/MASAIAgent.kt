package roles

import ai.koog.agents.core.agent.AIAgent

interface MASAIAgent {
    val agent : AIAgent
    fun runAgent(msg:String): String
}