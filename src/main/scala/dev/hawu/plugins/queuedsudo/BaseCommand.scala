package dev.hawu.plugins.queuedsudo

import dev.hawu.plugins.api.commands.{AbstractCommandClass, CommandArgument, CommandSource}
import dev.hawu.plugins.queuedsudo.commands.{GroupsCommand, ReloadCommand}
import dev.hawu.plugins.queuedsudo.LocalI18n.*
import org.bukkit.plugin.java.JavaPlugin

import java.util
import scala.jdk.CollectionConverters.*

/**
 * Command handler for the main command of the plugin.
 *
 * @param pl the plugin responsible
 */
class BaseCommand(private val pl: JavaPlugin) extends AbstractCommandClass("queuedsudo") :

   allowAny()
   setPermission("queued-sudo.main")
   register(pl)

   bind(GroupsCommand())
   bind(ReloadCommand())

   override def run(sender: CommandSource, args: CommandArgument): Unit = sender.tl("help.base")

   override def tab(sender: CommandSource, args: CommandArgument): util.List[String] = List(
      "groups",
      "reload",
   ).asJava
