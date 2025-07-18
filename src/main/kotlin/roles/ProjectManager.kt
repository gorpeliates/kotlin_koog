package roles

import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class ProjectManager(name : String = "ProjectManager Agent",
                     systemPrompt : String = "You are a project manager. You should break down the tasks given the product design."
                             + "You can access what each other agent does by using the getAgentDetails tool." +
                             "You can ask questions to these agents to prepare the tasks, if you need additional information to do so.") : MASAIAgent(name, systemPrompt) {

    override fun toString(): String {
        return "ProjectManager(name='$name')"
    }

}
