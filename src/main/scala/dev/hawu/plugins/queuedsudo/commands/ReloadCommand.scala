package dev.hawu.plugins.queuedsudo.commands

import dev.hawu.plugins.api.I18n
import dev.hawu.plugins.api.commands.{AbstractCommandClass, CommandArgument, CommandSource}
import dev.hawu.plugins.queuedsudo.LocalI18n
import dev.hawu.plugins.queuedsudo.LocalI18n.*

/**
 * The command to handle reloading the messages and configuration
 * files.
 */
class ReloadCommand extends AbstractCommandClass("reload"):

   allowAny()
   setPermission("queued-sudo.reload")
   
   override def run(sender: CommandSource, args: CommandArgument): Unit =
      LocalI18n.reload()
      sender.tl("reloaded")
