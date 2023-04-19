package bot

import bot.commands.CommandPackage
import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.jdabuilder.createJDA
import dev.minn.jda.ktx.util.SLF4J
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.security.auth.login.LoginException

/**
 * @author Arthur Behesnilian
 * 4:34 PM | 1/7/2023
 */
abstract class AbstractBot(
    private val name: String,
    /**
     * The Discord Bot token provided on the Discord developer portal
     */
    private val token: String,
    /**
     * Creates a separate thread to handle I/O operations
     * when this bot is created
     */
    private val dedicatedIoService: Boolean = false,
    /**
     * A set of [GatewayIntent]s that this bot should have access to
     */
    private val gatewayIntents: HashSet<GatewayIntent> = hashSetOf()
) {

    protected val logger by SLF4J(name)

    protected val jda: JDA
    protected lateinit var commandPackage: CommandPackage
    protected val channels = ConcurrentHashMap<String, TextChannel>()
    private lateinit var ioExecutor: ExecutorService

    init {
        jda = createJDA(
            token = this.token,
            enableCoroutines = true,
            intents = gatewayIntents
        ) {
            this.setMemberCachePolicy(MemberCachePolicy.NONE)
            this.disableCache(CacheFlag.values().toList())
            this.setChunkingFilter(ChunkingFilter.NONE)
            this.setLargeThreshold(50)
        }.also { jda ->
            jda.listener<ReadyEvent> {
                logger.info("Status: Ready")
                commandPackage = CommandPackage(jda)
                onReady(jda)
            }
        }

        if (dedicatedIoService) {
            ioExecutor = Executors.newSingleThreadExecutor()
        }
    }

    @Throws(LoginException::class, InterruptedException::class)
    protected fun getChannel(name: String): TextChannel? {
        if (channels.containsKey(name)) return channels[name]

        val foundChannels = jda.getTextChannelsByName(name, true)
        if (foundChannels.isEmpty()) {
            logger.error("No discord channel found with name: $name")
            return null
        }
        val channel = foundChannels[0]
        channels[name] = channel
        return channels[name]
    }

    fun queueMessage(channelName: String, message: String) {
        val channel = getChannel(channelName) ?: throw RuntimeException("Channel '$channelName' does not exist.")
        if (this::ioExecutor.isInitialized) {
            this.ioExecutor.submit {
                channel.sendMessage(message).queue()
            }
            return
        }
        channel.sendMessage(message).queue()
    }

    abstract fun onReady(jda: JDA)

}