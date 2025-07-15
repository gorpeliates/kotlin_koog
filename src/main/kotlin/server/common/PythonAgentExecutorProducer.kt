package server.common

import ai.koog.agents.core.agent.AIAgent
import io.a2a.server.agentexecution.AgentExecutor
import io.a2a.server.agentexecution.RequestContext
import io.a2a.server.events.EventQueue
import io.a2a.server.tasks.TaskUpdater
import io.a2a.spec.Message
import io.a2a.spec.Task
import io.a2a.spec.TaskNotCancelableError
import io.a2a.spec.TaskState
import io.a2a.spec.TextPart
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking

@ApplicationScoped
class PythonAgentExecutorProducer @Inject constructor(
    private val agent: AIAgent
) {
    @Produces
    fun agentExecutor(): AgentExecutor = PythonAgentExecutor(agent)

    private class PythonAgentExecutor(private val ai_agent: AIAgent) : AgentExecutor {

        override fun execute(
            context: RequestContext,
            eventQueue: EventQueue?
        ) {
            val updater = TaskUpdater(context, eventQueue)

            val userMessage :String = extractTextFromMessage(context.message)

            runBlocking {
                val response: String? = ai_agent.runAndGetResult(userMessage)
                println(response)
            }
            updater.complete()
        }

        override fun cancel(
            context: RequestContext?,
            eventQueue: EventQueue?
        ) {
            val task: Task = context!!.task

            if (task.status.state() === TaskState.CANCELED) {
                // task already cancelled
                throw TaskNotCancelableError()
            }

            if (task.status.state() === TaskState.COMPLETED) {
                // task already completed
                throw TaskNotCancelableError()
            }


            // cancel the task
            val updater = TaskUpdater(context, eventQueue)
            updater.cancel()
        }

        private fun extractTextFromMessage(message: Message): String {
            val textBuilder = StringBuilder()
            if (message.parts != null) {
                for (part in message.parts) {
                    if (part is TextPart) {
                        textBuilder.append(part.text)
                    }
                }
            }
            return textBuilder.toString()
        }

    }
}