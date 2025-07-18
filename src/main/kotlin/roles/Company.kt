package roles

import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class Company(name : String = "Company Agent",
              systemPrompt : String = "You are a software company. You should communicate with the other agents in the company to create the workflow for building a software."
                      + "You can access what each other agent does by using the getAgentDetails tool."
) : MASAIAgent(name, systemPrompt) {

    override fun toString(): String {
        return "Company(name='$name')"
    }

}