import kotlinx.coroutines.runBlocking
import roles.Engineer

fun main(args: Array<String>) {

    val engineerAgent = Engineer("Engineer agent","Write code")
    runBlocking {
        engineerAgent.agent.run("Use all your tools sequentially" )
    }

}