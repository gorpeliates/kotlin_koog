import kotlinx.coroutines.runBlocking
import roles.Company


fun main(args: Array<String>) {

    val companyAgent = Company("Company Agent")
    runBlocking {
        companyAgent.agent.run("Send a message to an agent at port sendmessage/10004" )
    }

}