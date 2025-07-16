package server

import io.a2a.spec.AgentCard
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import roles.AgentCardFactory

@Controller
class MASController {

        @PostMapping("/sendmessage/{agentId}")
    fun sendMessage(
        @PathVariable("agentId") message: String,
        @RequestBody body: Map<String, String>
    ): String{
        return body["message"] as String
    }

    @GetMapping("/.well-known")
    @ResponseBody
    fun agentCards() : List<AgentCard> {
        val response = AgentCardFactory().getAllAgentCards()
        println(response)
        return response
    }

}