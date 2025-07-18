import kotlinx.coroutines.runBlocking
import roles.Engineer


fun main(args: Array<String>) {

    val companyAgent = Engineer("Engineer Agent")
    runBlocking {
        println(companyAgent.agent.toolRegistry.tools.forEach { tool -> println(tool.descriptor) })
        companyAgent.agent.run("Hello" )
    }

}

//while(response != message) {
//    val response = agent.run(message)
//
//    reciever, sentmessage = method(response)
//    sendmessage(reciever, sentmessage)
//}