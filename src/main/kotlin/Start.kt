import kotlinx.coroutines.runBlocking
import roles.Company
import roles.Engineer


fun main(args: Array<String>) {

    val companyAgent = Engineer("Engineer Agent")
    runBlocking {
        println(companyAgent.agent.toolRegistry.tools.forEach { tool -> println(tool.descriptor) })
        companyAgent.agent.run("Hello" )
    }

}