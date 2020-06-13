import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.core.event.gateway.ReadyEvent
import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import com.gitlab.kordlib.core.on
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import java.io.File
import java.util.*



suspend fun main() {
    val properties = loadProperties()
    val client = Kord(properties.getProperty("TOKEN"))
    client.on<ReadyEvent> {
        println("Logged in")
        client.editPresence {
            playing(properties.getProperty("GAME"))
        }
    }
    client.on<MessageCreateEvent> {
        val content = message.content
        //if null, it's not a singular emote message so we just abort
        val url = searchMessageForEmote(content) ?: return@on
        message.getChannel().createEmbed {
            image = url
            footer {
                icon = message.author?.avatar?.url ?: message.author?.avatar?.defaultUrl
                text = message.author?.tag ?: "Invalid User"
            }
        }
    }
    client.login()
}

fun loadProperties(): Properties {
    val properties: Properties = Properties()
    properties.load(File("config.properties").inputStream())
    return properties
}

//maybe find better name
//checks if message fits either an animated message or regular emote syntax
fun searchMessageForEmote(content: String): String? {
    //IT'S NOT LIKE I WANT TO MATCH YOU OR ANYTHING BAKA
    val patternEmote = Regex("<:[^><]+:(\\d+)>")
    if (patternEmote.matches(content)) {
        val (emoteid) = patternEmote.find(content)!!.destructured
        return "https://cdn.discordapp.com/emojis/$emoteid.png?v=1"
    }
    val patternGif = Regex("<a:[^><]+:(\\d+)>")
    if (patternGif.matches(content)) {
        val (emoteid) = patternGif.find(content)!!.destructured
        return "https://cdn.discordapp.com/emojis/$emoteid.gif?v=1"
    }
    return null
}

suspend fun translateEngToJap(text: String) {
    translate(text, "EN", "JA")
}

suspend fun translateJapToEng(text: String) {
    translate(text, "JA", "EN")
}

suspend fun translate(text: String, sourceLang: String, targetLang: String) {
    val url = "https://www2.deepl.com/jsonrpc"
    val timestamp=System.currentTimeMillis()
    val query="{\"jsonrpc\":\"2.0\",\"method\" : \"LMT_handle_jobs\",\"params\":{\"jobs\":[{\"kind\":\"default\",\"raw_en_sentence\":\"$text\",\"raw_en_context_before\":[],\"raw_en_context_after\":[],\"quality\":\"fast\"}],\"lang\":{\"user_preferred_langs\":[\"DE\",\"NL\",\"ZH\",\"EN\",\"JA\"],\"source_lang_user_selected\":\"$sourceLang\",\"target_lang\":\"$targetLang\"},\"priority\":20,\"commonJobParams\":{},\"timestamp\":$timestamp},\"id\":86190023}"
    val client = HttpClient()
    val response=client.post<String>(url){ body=query }
}