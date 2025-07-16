import kotlinx.coroutines.runBlocking
import roles.Engineer

fun main(args: Array<String>) {

    val engineerAgent = Engineer("Engineer agent","Write code")
    runBlocking {
        engineerAgent.agent.run("Send a message to another agent via HTTP POST request. Use your tools.")
    }

}