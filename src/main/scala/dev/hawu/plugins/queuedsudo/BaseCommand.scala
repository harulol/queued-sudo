package dev.hawu.plugins.queuedsudo

import dev.hawu.plugins.api.commands.{AbstractCommandClass, CommandArgument, CommandSource}
import org.bukkit.plugin.java.JavaPlugin

class BaseCommand(private val pl: JavaPlugin) extends AbstractCommandClass("queuedsudo"):
   
   allowAny()
   setPermission("queued-sudo.main")
   register(pl)

   override def run(sender: CommandSource, args: CommandArgument): Unit = ()
