package server

import io.a2a.server.PublicAgentCard
import io.a2a.spec.AgentCapabilities
import io.a2a.spec.AgentCard
import io.a2a.spec.AgentSkill
import io.github.cdimascio.dotenv.dotenv
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import roles.Architect
import roles.Orchestrator
import roles.Engineer
import roles.MASAIAgent
import roles.ProductManager
import roles.Tester

@Scope("prototype")
@Component
class AgentServer {
    private val SERVER_URL = dotenv()["SPRING_SERVER_URL"]

    private final val map : Map<String, MASAIAgent> = mapOf(
        "engineer" to Engineer(),
        "productmanager" to ProductManager(),
        "tester" to Tester(),
        "architect" to Architect()
    )

    private val orchestrator = Orchestrator()
    @PublicAgentCard
    fun engineerAgentCard(): AgentCard {
        return AgentCard.Builder()
            .name("engineer")
            .description("Responsible for writing efficient code.")
            .url("http://localhost:$SERVER_URL/sendmessage/engineer")
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
            .name("productmanager")
            .description("Responsible for product strategy and roadmap planning")
            .url("http://localhost:$SERVER_URL/sendmessage/productmanager")
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
    fun testerAgentCard(): AgentCard {
        return AgentCard.Builder()
            .name("tester")
            .description("Responsible for testing the software and ensuring its quality.")
            .url("http://localhost:$SERVER_URL/sendmessage/tester")
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
                        .id("code_quality")
                        .name("Code Quality Assurance")
                        .description("Outlines the improvements needed in the code to ensure code quality standards.")
                        .tags(mutableListOf<String?>("code", "quality", "review"))
                        .build(),
                    AgentSkill.Builder()
                        .id("unit_testing")
                        .name("Unit Testing")
                        .description("Writes a list of unit tests to ensure the software works as intended.")
                        .tags(mutableListOf<String?>("code", "unit test", "testing"))
                        .build()
                )
            )
            .build()
    }

    @PublicAgentCard
    fun architectAgentCard(): AgentCard {
        return AgentCard.Builder()
            .name("architect")
            .description("Responsible for designing the software ")
            .url("http://localhost:$SERVER_URL/sendmessage/architect")
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



    @PublicAgentCard
    fun orchestratorAgentCard(): AgentCard {
        return AgentCard.Builder()
            .name("Company Agent")
            .description("Responsible for demanding the software and supervising")
            .url("http://localhost:$SERVER_URL/sendmessage/orchestrator")
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
                        .id("supervision")
                        .name("Company Supervision")
                        .description("Supervises the employees in the company to ensure responsibilities are distributed and completed correctly.")
                        .tags(mutableListOf<String?>("supervision", "verification", "coordination"))
                        .build(),
                )
            )
            .build()
    }

    fun getAllAgentCards(): MutableList<AgentCard> {
        return arrayListOf(
            engineerAgentCard(),
            productManagerAgentCard(),
            testerAgentCard(),
            architectAgentCard(),
            orchestratorAgentCard()
        )
    }

    fun getAgent(agentId: String) : MASAIAgent {
        return map[agentId]!!
    }


    fun startWorkflow(task:String): String {
        return orchestrator.startTask(task)
    }


}