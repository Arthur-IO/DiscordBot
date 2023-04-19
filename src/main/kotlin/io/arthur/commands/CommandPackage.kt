package io.arthur.commands

import net.dv8tion.jda.api.JDA


/**
 * @author Arthur Behesnilian
 * 5:15 PM | 1/7/2023
 */
class CommandPackage(val jda: JDA) {

    private val commandPackage = arrayListOf<Command>()

    fun addCommands(vararg commands: Command) {
        commandPackage.addAll(commands)
        jda.addEventListener(*commandPackage.toTypedArray())
    }

    fun subscribeCommandsToGuild(guildId: Long) {
        val guild = jda.getGuildById(guildId) ?: throw IllegalStateException("Guild with id=$guildId was not found.")
        guild.updateCommands().addCommands(commandPackage.map(Command::build)).queue()
    }

}