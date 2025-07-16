import kotlinx.coroutines.runBlocking
import roles.Engineer

fun main(args: Array<String>) {

    val engineerAgent = Engineer("Engineer agent","Write code")
    runBlocking {
        engineerAgent.agent.runAndGetResult(
                "Use your tools to give me the list of the other agents")
    }

}