package bot.commands

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

/**
 * @author Arthur Behesnilian
 * 5:14 PM | 1/7/2023
 */
abstract class Command(
    val desc: String
) : ListenerAdapter() {

    val command = this.javaClass.simpleName
        .lowercase()
        .replace("command", "")

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name.equals(command, ignoreCase = true)) {
            processCommand(event)
        }
    }

    abstract fun processCommand(event: SlashCommandInteractionEvent)

    protected fun default() = Commands.slash(command, desc)

    /**
     * Creates an instance of the [CommandData] to be used for building purposes
     */
    protected fun create(
        builder: SlashCommandData.() -> Unit
    ): SlashCommandData {
        val commandDate = Commands.slash(command, desc)
        builder(commandDate)
        return commandDate
    }

    /**
     * The final build of the [CommandData] that is passed to a guild.
     */
    abstract fun build(): SlashCommandData

}