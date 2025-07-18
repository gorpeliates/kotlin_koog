import kotlinx.coroutines.runBlocking
import roles.Company
import roles.Engineer


fun main(args: Array<String>) {

    val companyAgent = Engineer("Company Agent")
    runBlocking {
        companyAgent.agent.run("Send a test message to the agent with url: http://localhost:8080/sendmessage/10004" )
    }

}