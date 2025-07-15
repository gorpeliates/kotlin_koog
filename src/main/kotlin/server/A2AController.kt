package server


import io.a2a.spec.AgentCard
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import server.common.PythonAgentCardProducer

@Controller
class A2AController {

    private val pythonAgentCardProducer: PythonAgentCardProducer

    @Autowired
    constructor(pythonAgentCardProducer: PythonAgentCardProducer) {
        this.pythonAgentCardProducer = pythonAgentCardProducer
        println("A2AController started")
    }

    @GetMapping("/a2a/test")
    fun test() : ResponseEntity<String> {
        return ResponseEntity.ok("OK")
    }

    @GetMapping("/a2a/.well-known")
    fun a2a_get() : ResponseEntity<AgentCard> {
        return ResponseEntity.ok(pythonAgentCardProducer.agentCard())
    }

}