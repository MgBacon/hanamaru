import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.core.event.gateway.ReadyEvent
import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import com.gitlab.kordlib.core.on
import java.io.File
import java.util.*


suspend fun main() {
    val properties=loadProperties()
    val client = Kord(properties.getProperty("TOKEN"))
    client.on<ReadyEvent>{
        println("Logged in")
    }
    client.on<MessageCreateEvent> {
        val content=message.content
        //IT'S NOT LIKE I WANT TO MATCH YOU OR ANYTHING BAKA
        val pattern=Regex("<:[^><]+:(\\d+)>")
        if(pattern.matches(content)) {
            val (emoteid) = pattern.find(content)!!.destructured
            val url="https://cdn.discordapp.com/emojis/$emoteid.png?v=1"
            val username=message.author?.tag ?: "Invalid User"
            val channel = message.getChannel()
            channel.createEmbed {
                image=url
                footer  {
                    icon=message.author?.avatar?.url ?: message.author?.avatar?.defaultUrl
                    text=username
                }
            }
        }
    }
    client.login()
}

fun loadToken(): String {
    val properties: Properties = Properties()
    properties.load(File("config.properties").inputStream())
    return properties.getProperty("TOKEN")
}
fun loadProperties(): Properties {
        val properties: Properties = Properties()
        properties.load(File("config.properties").inputStream())
        return properties
}

