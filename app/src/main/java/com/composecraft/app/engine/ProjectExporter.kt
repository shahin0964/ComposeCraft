package com.composecraft.app.engine

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import com.composecraft.app.model.AppProject
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

object ProjectExporter {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    /**
     * Copies code or text to system clipboard.
     */
    fun copyToClipboard(context: Context, label: String, content: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, content)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
    }

    /**
     * Shares text via Android system share sheet.
     */
    fun shareText(context: Context, title: String, content: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, content)
            putExtra(Intent.EXTRA_TITLE, title)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    /**
     * Saves the entire self-contained Android project structure to phone's Download directory
     * so any AI Agent or developer can build it immediately.
     */
    fun exportFullProjectToDownloads(context: Context, project: AppProject): Result<File> {
        return try {
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                ?: context.getExternalFilesDir(null)
                ?: context.filesDir

            val safeProjectName = project.name.replace("[^a-zA-Z0-9_-]".toRegex(), "_")
            val targetDir = File(downloadDir, "ComposeCraft_$safeProjectName")
            targetDir.mkdirs()

            // 1. Write PROMPT_FOR_AI.md
            val promptFile = File(targetDir, "PROMPT_FOR_AI.md")
            promptFile.writeText(ComposeCodeGenerator.generateAIPrompt(project))

            // 2. Write README.md
            val readmeFile = File(targetDir, "README.md")
            readmeFile.writeText(generateReadme(project))

            // 3. Write project JSON backup
            val jsonBackupFile = File(targetDir, "project_data.json")
            jsonBackupFile.writeText(gson.toJson(project))

            // 4. Write Root build.gradle.kts and settings.gradle.kts
            File(targetDir, "settings.gradle.kts").writeText(generateSettingsGradle(project))
            File(targetDir, "build.gradle.kts").writeText(generateRootBuildGradle())

            // 5. App Module directory
            val appDir = File(targetDir, "app")
            val srcDir = File(appDir, "src/main")
            val packagePath = project.packageName.replace('.', '/')
            val javaDir = File(srcDir, "java/$packagePath")
            val screensDir = File(javaDir, "ui/screens")
            screensDir.mkdirs()

            File(appDir, "build.gradle.kts").writeText(generateAppBuildGradle(project))
            File(srcDir, "AndroidManifest.xml").writeText(generateManifest(project))

            // 6. Write MainActivity.kt with NavHost
            File(javaDir, "MainActivity.kt").writeText(generateMainActivity(project))

            // 7. Write each screen .kt file
            for (screen in project.screens) {
                val fileName = screen.name.replace(" ", "") + "View.kt"
                val screenFile = File(screensDir, fileName)
                screenFile.writeText(ComposeCodeGenerator.generateScreenCode(screen, project.packageName))
            }

            Result.success(targetDir)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateReadme(project: AppProject): String {
        return """
# 🚀 ${project.name}
*Generated automatically by **ComposeCraft** - Visual UI/UX Designer for Android*

## 📱 About This App
This is a full native Android project built with **Jetpack Compose (Kotlin)**.
It contains **${project.screens.size} screens** with modern styling and real-time animations.

## 🤖 Instructions for AI Agents (Claude / Gemini / ChatGPT / Copilot)
To compile or enhance this project:
1. Open this directory in your favorite IDE or build with `./gradlew assembleDebug`.
2. All screens are located in `app/src/main/java/${project.packageName.replace('.', '/')}/ui/screens/`.
3. Navigation between screens is managed in `MainActivity.kt`.
4. Read `PROMPT_FOR_AI.md` for full component specifications.
""".trimIndent()
    }

    private fun generateSettingsGradle(project: AppProject): String {
        return """
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "${project.name.replace(" ", "")}"
include(":app")
""".trimIndent()
    }

    private fun generateRootBuildGradle(): String {
        return """
plugins {
    id("com.android.application") version "8.7.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
}
""".trimIndent()
    }

    private fun generateAppBuildGradle(project: AppProject): String {
        return """
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "${project.packageName}"
    compileSdk = 35

    defaultConfig {
        applicationId = "${project.packageName}"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.8.3")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
""".trimIndent()
    }

    private fun generateManifest(project: AppProject): String {
        return """
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application
        android:allowBackup="true"
        android:label="${project.name}"
        android:supportsRtl="true"
        android:theme="@android:style/Theme.Material.Light.NoActionBar">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
""".trimIndent()
    }

    private fun generateMainActivity(project: AppProject): String {
        val startScreen = project.currentScreen
        val startName = startScreen.name.replace(" ", "") + "View"
        val sb = StringBuilder()
        sb.appendLine("package ${project.packageName}")
        sb.appendLine()
        sb.appendLine("import android.os.Bundle")
        sb.appendLine("import android.widget.Toast")
        sb.appendLine("import androidx.activity.ComponentActivity")
        sb.appendLine("import androidx.activity.compose.setContent")
        sb.appendLine("import androidx.compose.material3.MaterialTheme")
        sb.appendLine("import androidx.navigation.compose.NavHost")
        sb.appendLine("import androidx.navigation.compose.composable")
        sb.appendLine("import androidx.navigation.compose.rememberNavController")
        for (s in project.screens) {
            val fn = s.name.replace(" ", "") + "View"
            sb.appendLine("import ${project.packageName}.ui.screens.$fn")
        }
        sb.appendLine()
        sb.appendLine("class MainActivity : ComponentActivity() {")
        sb.appendLine("    override fun onCreate(savedInstanceState: Bundle?) {")
        sb.appendLine("        super.onCreate(savedInstanceState)")
        sb.appendLine("        setContent {")
        sb.appendLine("            MaterialTheme {")
        sb.appendLine("                val navController = rememberNavController()")
        sb.appendLine("                NavHost(navController = navController, startDestination = \"${project.screens.first().id}\") {")
        for (s in project.screens) {
            val fn = s.name.replace(" ", "") + "View"
            sb.appendLine("                    composable(\"${s.id}\") {")
            sb.appendLine("                        $fn(")
            sb.appendLine("                            onNavigate = { target -> navController.navigate(target) },")
            sb.appendLine("                            onShowToast = { msg -> Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show() }")
            sb.appendLine("                        )")
            sb.appendLine("                    }")
        }
        sb.appendLine("                }")
        sb.appendLine("            }")
        sb.appendLine("        }")
        sb.appendLine("    }")
        sb.appendLine("}")
        return sb.toString()
    }
}
