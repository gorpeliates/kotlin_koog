package roles

import ai.koog.agents.core.agent.AIAgent

class Boss(val name: String, val request: String, val agent: AIAgent) {


    override fun toString(): String {
        return "Boss(name='$name', request='$request', agent=$agent)"
    }


}