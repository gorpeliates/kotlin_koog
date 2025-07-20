import kotlinx.coroutines.runBlocking
import roles.Engineer


fun main(args: Array<String>) {

    val companyAgent = Engineer("Engineer Agent")
    runBlocking {
        println(companyAgent.agent.toolRegistry.tools.forEach { tool -> println(tool.descriptor) })
        companyAgent.agent.run("Use your tool to create a random number. Always use a tool. Return the answer to me in the format:" +
                "Generated Number: {number generated}" )
    }
}
