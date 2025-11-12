import org.gradle.api.Project
import java.io.FileInputStream
import java.util.Properties

object BuildConfigUtils {

    fun readLocalProperty(project: Project, propertyKey: String, defaultValue: String = ""): String {
        val localPropertiesFile = project.rootProject.file("local.properties")
        
        if (!localPropertiesFile.canRead()) {
            println("Warning: local.properties file not found or not readable.")
            return defaultValue
        }

        val properties = Properties()
        properties.load(FileInputStream(localPropertiesFile))
        return properties.getProperty(propertyKey, defaultValue)
    }

    fun readGiphyApiKey(project: Project): String {
        return readLocalProperty(project, "giphy.api.key", "")
    }

    fun getGiphyApiBaseUrl(): String = "https://api.giphy.com/"
}

