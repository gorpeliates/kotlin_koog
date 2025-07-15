package server.roles

import ai.koog.agents.core.agent.AIAgent

class Boss(name: String, request: String, agent: AIAgent) {


    val name: String = name
    val request: String = request
    val agent: AIAgent = agent

    override fun toString(): String {
        return "Boss(name='$name', request='$request', agent=$agent)"
    }


}