package dev.hawu.plugins.queuedsudo.commands

import dev.hawu.plugins.api.commands.{AbstractCommandClass, CommandArgument, CommandSource}

class GroupsCommand extends AbstractCommandClass("groups"):
   
   alias("g")
   setPermission("queued-sudo.groups.main")

   override def run(sender: CommandSource, args: CommandArgument): Unit =
      ()
