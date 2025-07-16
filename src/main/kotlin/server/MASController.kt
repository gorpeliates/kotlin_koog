package server

import io.a2a.spec.AgentCard
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import roles.AgentServer

@Controller
class MASController @Autowired constructor(private val agentServer: AgentServer){

        @PostMapping("/sendmessage/{agentId}")
    fun sendMessage(
            @PathVariable("agentId") agent_id: String,
            @RequestBody body: Map<String, String>
    ): ResponseEntity<String?> {
            try {
                val agent = agentServer.getAgent(Integer.parseInt(agent_id))
                val response : String = agent.runAgent(body["message"] as String)
                return ResponseEntity(response, HttpStatus.OK)
            } catch (e : Exception) {
                return ResponseEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
            }
    }

    @GetMapping("/.well-known")
    @ResponseBody
    fun agentCards() : ResponseEntity<List<AgentCard>> {
        try {
            val response = agentServer.getAllAgentCards()
            return ResponseEntity(response, HttpStatus.OK)
        } catch (_ : Exception) {
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}