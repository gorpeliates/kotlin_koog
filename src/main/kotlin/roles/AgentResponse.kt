package roles

data class AgentResponse(
    val message: String,
    val statusCode: Int,
    val response: String
)