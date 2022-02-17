package dev.hawu.plugins.queuedsudo

import dev.hawu.plugins.api.commands.{AbstractCommandClass, CommandArgument, CommandSource}
import dev.hawu.plugins.queuedsudo.commands.{ReloadCommand, GroupsCommand}
import dev.hawu.plugins.queuedsudo.LocalI18n.*
import org.bukkit.plugin.java.JavaPlugin

class BaseCommand(private val pl: JavaPlugin) extends AbstractCommandClass("queuedsudo") :

   allowAny()
   setPermission("queued-sudo.main")
   register(pl)

   bind(GroupsCommand())
   bind(ReloadCommand())

   override def run(sender: CommandSource, args: CommandArgument): Unit = sender.tl("help.base")
