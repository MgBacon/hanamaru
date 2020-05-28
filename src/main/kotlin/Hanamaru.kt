import com.gitlab.kordlib.common.entity.Permission
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.channel.createMessage
import com.gitlab.kordlib.core.entity.channel.GuildMessageChannel
import com.gitlab.kordlib.core.event.gateway.ReadyEvent
import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import com.gitlab.kordlib.core.on
import com.gitlab.kordlib.rest.builder.message.EmbedBuilder
import java.io.File
import java.util.*


suspend fun main() {
    val client = Kord(loadToken())
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
            val ownembed= EmbedBuilder()
            ownembed.apply {
                image=url
                footer  {
                    icon=message.author?.avatar?.url ?: message.author?.avatar?.defaultUrl
                    text=username
                }
            }
            if (channel !is GuildMessageChannel){ //if not guild, then DM
                channel.createMessage { embed=ownembed}
            }else{
                val permissions = channel.getEffectivePermissions(kord.selfId)
                //delete original message if you got the perm
                /*
                if(Permission.ManageMessages in permissions){
                    message.delete()
                }
                */
                //check if I can actually post an embed
                if(Permission.EmbedLinks in permissions){
                    channel.createMessage { embed=ownembed }
                }else{ //if not just post the URL
                    channel.createMessage("$url by $username")
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

