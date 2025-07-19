import kotlinx.coroutines.runBlocking
import roles.Engineer


fun main(args: Array<String>) {

    val companyAgent = Engineer("Engineer Agent")
    runBlocking {
        println(companyAgent.agent.toolRegistry.tools.forEach { tool -> println(tool.descriptor) })
        companyAgent.agent.run("You must always use a tool. Do not respond with a regular message. If no tool fits, return a default tool call with empty parameters" )
    }

}

//while(response != message) {
//    val response = agent.run(message)
//
//    reciever, sentmessage = method(response)
//    sendmessage(reciever, sentmessage)
//}