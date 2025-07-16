package server

import io.a2a.server.PublicAgentCard
import io.a2a.spec.AgentCapabilities
import io.a2a.spec.AgentCard
import io.a2a.spec.AgentSkill
import io.github.cdimascio.dotenv.dotenv
import jakarta.inject.Singleton
import org.springframework.stereotype.Component
import roles.Architect
import roles.Engineer
import roles.MASAIAgent
import roles.ProductManager
import roles.ProjectManager

@Singleton
@Component
class AgentServer {
    private val SERVER_URL = dotenv()["SPRING_SERVER_URL"]
    private val id_to_agents : Map<Int, MASAIAgent> = mapOf(
        10001 to Engineer("Engineer agent"),
        10004 to ProductManager("ProductManager agent"),
        10007 to ProjectManager("ProjectManager agent"),
        10011 to Architect("Architer agent"),
    )
    @PublicAgentCard
    fun engineerAgentCard(): AgentCard {
        return AgentCard.Builder()
            .name("Engineer Agent")
            .description("Responsible for writing code ")
            .url("http://localhost:$SERVER_URL/sendmessage/10001")
            .version("1.0.0")
            .capabilities(
                AgentCapabilities.Builder()
                    .streaming(true)
                    .pushNotifications(false)
                    .stateTransitionHistory(false)
                    .build()
            )
            .defaultInputModes(mutableListOf<String?>("text"))
            .defaultOutputModes(mutableListOf<String?>("text"))
            .skills(
                mutableListOf<AgentSkill?>(
                    AgentSkill.Builder()
                        .id("code_python")
                        .name("Write code")
                        .description("Helps with writing python code.")
                        .tags(mutableListOf<String?>("code"))
                        .build()
                )
            )
            .build()
    }
    @PublicAgentCard
    fun productManagerAgentCard(): AgentCard {
        return AgentCard.Builder()
            .name("Product Manager Agent")
            .description("Responsible for product strategy and roadmap planning")
            .url("http://localhost:$SERVER_URL/sendmessage/10004")
            .version("1.0.0")
            .capabilities(
                AgentCapabilities.Builder()
                    .streaming(true)
                    .pushNotifications(true)
                    .stateTransitionHistory(true)
                    .build()
            )
            .defaultInputModes(mutableListOf<String?>("text"))
            .defaultOutputModes(mutableListOf<String?>("text", "document"))
            .skills(
                mutableListOf<AgentSkill?>(
                    AgentSkill.Builder()
                        .id("product_roadmap")
                        .name("Create Product Roadmap")
                        .description("Develops strategic product roadmaps and feature prioritization.")
                        .tags(mutableListOf<String?>("strategy", "planning", "roadmap"))
                        .build(),
                    AgentSkill.Builder()
                        .id("market_analysis")
                        .name("Market Analysis")
                        .description("Analyzes market trends and competitive landscape.")
                        .tags(mutableListOf<String?>("market", "analysis", "competition"))
                        .build()
                )
            )
            .build()
    }
    @PublicAgentCard
    fun projectManagerAgentCard(): AgentCard {
        return AgentCard.Builder()
            .name("Project Manager Agent")
            .description("Responsible for project coordination and team management")
            .url("http://localhost:$SERVER_URL/sendmessage/10007")
            .version("1.0.0")
            .capabilities(
                AgentCapabilities.Builder()
                    .streaming(true)
                    .pushNotifications(true)
                    .stateTransitionHistory(true)
                    .build()
            )
            .defaultInputModes(mutableListOf<String?>("text"))
            .defaultOutputModes(mutableListOf<String?>("text", "schedule"))
            .skills(
                mutableListOf<AgentSkill?>(
                    AgentSkill.Builder()
                        .id("project_planning")
                        .name("Project Planning")
                        .description("Creates detailed project plans and timelines.")
                        .tags(mutableListOf<String?>("planning", "scheduling", "coordination"))
                        .build(),
                    AgentSkill.Builder()
                        .id("resource_management")
                        .name("Resource Management")
                        .description("Manages team resources and task allocation.")
                        .tags(mutableListOf<String?>("resources", "allocation", "management"))
                        .build()
                )
            )
            .build()
    }

    @PublicAgentCard
    fun architectAgentCard(): AgentCard {
        return AgentCard.Builder()
            .name("Architect Agent")
            .description("Responsible for designing the software ")
            .url("http://localhost:$SERVER_URL/sendmessage/10011")
            .version("1.0.0")
            .capabilities(
                AgentCapabilities.Builder()
                    .streaming(true)
                    .pushNotifications(false)
                    .stateTransitionHistory(false)
                    .build()
            )
            .defaultInputModes(mutableListOf<String?>("text"))
            .defaultOutputModes(mutableListOf<String?>("text"))
            .skills(
                mutableListOf<AgentSkill?>(
                    AgentSkill.Builder()
                        .id("software_design")
                        .name("Plan software")
                        .description("Creates the architecture and design of a software, given the requirements.")
                        .tags(mutableListOf<String?>("plan", "design", "software", "architecture"))
                        .build()
                )
            )
            .build()
    }

    fun getAllAgentCards(): MutableList<AgentCard> {
        return arrayListOf(
            engineerAgentCard(),
            productManagerAgentCard(),
            projectManagerAgentCard(),
            architectAgentCard()
        )
    }

    fun getAgent(agentId: Int) : MASAIAgent {
        return id_to_agents[agentId]!!
    }


}