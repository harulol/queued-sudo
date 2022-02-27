package dev.hawu.plugins.queuedsudo.commands

import dev.hawu.plugins.api.commands.{AbstractCommandClass, CommandArgument, CommandSource}
import dev.hawu.plugins.queuedsudo.LocalI18n.*
import dev.hawu.plugins.queuedsudo.commands.groups.{CreateCommand, ListCommand}

import java.util
import scala.jdk.CollectionConverters.*

class GroupsCommand extends AbstractCommandClass("groups") :

   alias("g")
   setPermission("queued-sudo.groups.main")

   bind(new CreateCommand)
   bind(new ListCommand)

   private val suggestions = List(
      "create", "new", "list",
   ).sorted

   override def run(sender: CommandSource, args: CommandArgument): Unit = sender.tl("help.groups.base")

   override def tab(sender: CommandSource, args: CommandArgument): util.List[String] =
      suggestions.filter(_.startsWith(Option(args.lastOrNull).getOrElse(""))).asJava
