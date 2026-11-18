package com.composecraft.app.engine

import android.content.Context
import com.composecraft.app.model.AppProject
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

object ProjectStorage {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    private fun getProjectsDir(context: Context): File {
        val dir = File(context.filesDir, "compose_craft_projects")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun saveProject(context: Context, project: AppProject) {
        try {
            val file = File(getProjectsDir(context), "${project.id}.json")
            file.writeText(gson.toJson(project))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadAllProjects(context: Context): List<AppProject> {
        val list = mutableListOf<AppProject>()
        try {
            val dir = getProjectsDir(context)
            dir.listFiles()?.filter { it.extension == "json" }?.forEach { file ->
                val project = gson.fromJson(file.readText(), AppProject::class.java)
                if (project != null) list.add(project)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun deleteProject(context: Context, projectId: String) {
        try {
            val file = File(getProjectsDir(context), "$projectId.json")
            if (file.exists()) file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun parseProjectFromJson(jsonString: String): AppProject? {
        return try {
            gson.fromJson(jsonString, AppProject::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
