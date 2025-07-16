import kotlinx.coroutines.runBlocking
import roles.Engineer

fun main(args: Array<String>) {

    val engineerAgent = Engineer("Engineer agent","Write code")
    runBlocking {
        engineerAgent.agent.run("Send a message to an agent at port sendmessage/10004" )
    }

}