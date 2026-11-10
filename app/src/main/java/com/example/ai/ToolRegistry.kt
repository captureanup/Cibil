package com.example.ai

import com.example.data.repository.BuildlabsRepository
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONObject

class ToolRegistry(private val repository: BuildlabsRepository) {

    val tools: List<ToolDefinition> = listOf(
        ToolDefinition(
            name = "get_user_profile",
            description = "Retrieves the current authenticated user's profile and active preferences.",
            parameters = emptyList(),
            requiresApproval = false,
            riskLevel = "Low"
        ),
        ToolDefinition(
            name = "list_projects",
            description = "Lists all current active projects in the user's BUILDLABS workspace.",
            parameters = emptyList(),
            requiresApproval = false,
            riskLevel = "Low"
        ),
        ToolDefinition(
            name = "get_project_details",
            description = "Retrieves specific details about a project given its project ID.",
            parameters = listOf(
                ToolParameter("projectId", "string", "The unique ID of the project", required = true)
            ),
            requiresApproval = false,
            riskLevel = "Low"
        ),
        ToolDefinition(
            name = "create_project",
            description = "Creates a new workspace project with name and description. Requires user authorization.",
            parameters = listOf(
                ToolParameter("name", "string", "Project title/name", required = true),
                ToolParameter("description", "string", "Detailed description of project goals", required = true)
            ),
            requiresApproval = true,
            riskLevel = "Medium"
        ),
        ToolDefinition(
            name = "create_action_card",
            description = "Generates an actionable card for user review (e.g. deadline reminders, draft action items).",
            parameters = listOf(
                ToolParameter("title", "string", "Title of the action card", required = true),
                ToolParameter("description", "string", "Description of what action is suggested", required = true),
                ToolParameter("priority", "string", "Priority level: High, Medium, or Low", required = false),
                ToolParameter("deadline", "string", "Suggested deadline date or timeframe", required = false)
            ),
            requiresApproval = false,
            riskLevel = "Low"
        ),
        ToolDefinition(
            name = "search_conversations",
            description = "Searches the titles and contents of past user conversations.",
            parameters = listOf(
                ToolParameter("query", "string", "Search keyword or phrase", required = true)
            ),
            requiresApproval = false,
            riskLevel = "Low"
        )
    )

    fun getTool(name: String): ToolDefinition? = tools.find { it.name == name }

    suspend fun executeTool(name: String, argsJson: String): String {
        return try {
            val json = if (argsJson.isNotBlank()) JSONObject(argsJson) else JSONObject()
            when (name) {
                "get_user_profile" -> {
                    val profile = repository.userProfile.firstOrNull()
                    val result = JSONObject().apply {
                        put("id", profile?.id ?: "usr_lead_01")
                        put("displayName", profile?.displayName ?: "Alex Carter")
                        put("email", profile?.email ?: "alex@buildlabs.ai")
                    }
                    result.toString()
                }

                "list_projects" -> {
                    val projects = repository.allProjects.firstOrNull() ?: emptyList()
                    val array = org.json.JSONArray()
                    projects.forEach { p ->
                        val item = JSONObject().apply {
                            put("id", p.id)
                            put("name", p.name)
                            put("description", p.description)
                            put("status", p.status)
                        }
                        array.put(item)
                    }
                    val result = JSONObject().put("projects", array)
                    result.toString()
                }

                "get_project_details" -> {
                    val id = json.optString("projectId")
                    val project = repository.getProjectById(id)
                    if (project != null) {
                        JSONObject().apply {
                            put("found", true)
                            put("id", project.id)
                            put("name", project.name)
                            put("description", project.description)
                            put("status", project.status)
                        }.toString()
                    } else {
                        JSONObject().apply {
                            put("found", false)
                            put("message", "Project with ID $id not found.")
                        }.toString()
                    }
                }

                "create_project" -> {
                    val nameArg = json.optString("name", "Untitled Project")
                    val descArg = json.optString("description", "")
                    val created = repository.createProject(name = nameArg, description = descArg)
                    JSONObject().apply {
                        put("success", true)
                        put("id", created.id)
                        put("name", created.name)
                        put("message", "Project created successfully.")
                    }.toString()
                }

                "create_action_card" -> {
                    val title = json.optString("title", "New Action Item")
                    val desc = json.optString("description", "")
                    val priority = json.optString("priority", "Medium")
                    val deadline = if (json.has("deadline")) json.optString("deadline") else null
                    val card = repository.createActionCard(
                        title = title,
                        description = desc,
                        source = "Buildlabs Agent Tool",
                        priority = priority,
                        deadline = deadline,
                        requiresApproval = false
                    )
                    JSONObject().apply {
                        put("success", true)
                        put("id", card.id)
                        put("title", card.title)
                        put("message", "Action card created and added to Home dashboard.")
                    }.toString()
                }

                "search_conversations" -> {
                    val query = json.optString("query", "").lowercase()
                    val convs = repository.allConversations.firstOrNull() ?: emptyList()
                    val matches = convs.filter { it.title.lowercase().contains(query) }
                    val array = org.json.JSONArray()
                    matches.forEach { c ->
                        array.put(JSONObject().apply {
                            put("id", c.id)
                            put("title", c.title)
                        })
                    }
                    JSONObject().put("matches", array).toString()
                }

                else -> {
                    JSONObject().apply {
                        put("error", "Unknown tool: $name")
                    }.toString()
                }
            }
        } catch (e: Exception) {
            JSONObject().apply {
                put("error", e.localizedMessage ?: "Execution error")
            }.toString()
        }
    }
}
