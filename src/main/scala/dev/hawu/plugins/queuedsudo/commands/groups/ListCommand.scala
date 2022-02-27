package dev.hawu.plugins.queuedsudo.commands.groups

import dev.hawu.plugins.api.commands.{AbstractCommandClass, CommandArgument, CommandLine, CommandSource}
import dev.hawu.plugins.queuedsudo.DataManager.{getGroup, getGroups, unpack}
import dev.hawu.plugins.queuedsudo.LocalI18n.*
import dev.hawu.plugins.queuedsudo.{DataManager, WorldGroup}

import java.util
import scala.jdk.CollectionConverters.*

/**
 * Command handler for the CLI to list all registered
 * world groups in the plugin.
 */
class ListCommand extends AbstractCommandClass("list") :

   setPermission("queued-sudo.groups.list")
   allowAny()
   setParser(CommandLine().withArgument("-n").withFlag("-c"))

   override def run(sender: CommandSource, args: CommandArgument): Unit =
      val (arguments, props) = args.parse().unpack
      if props.containsKey("-?") || props.containsKey("--help") then return sender.tl("help.groups.list")

      val count = Option(props.get("-n")).map(_.get(0)).map(_.toInt).getOrElse(-1)
      val compact = props.containsKey("-c")
      val groups = drop(DataManager.getGroups, count)
      val key = if compact then "view-group-compact" else "view-group"

      if groups.isEmpty then return sender.tl("no-groups")
      sender.tl("showing-groups", "count" -> groups.size, "total" -> DataManager.getGroups.size, "compact" -> (if compact then "&aon" else "&coff"))
      groups.foreach(g => sender.sendMessage(mkMsg(g, compact)))
   end run

   private def drop[T](iter: Iterable[T], n: Int): Iterable[T] = if iter.size <= n || n < 0 then iter else iter.drop(n)

   private def mkMsg(group: WorldGroup, compact: Boolean): String =
      if compact then "view-group-compact".tl("name" -> group.name, "icon" -> group.icon.name.toLowerCase, "worlds" -> group.worlds.size)
      else "view-group".tl("name" -> group.name, "icon" -> group.icon.name.toLowerCase, "worlds" -> group.worlds.mkString(", "))

   override def tab(sender: CommandSource, args: CommandArgument): util.List[String] = args.get(args.size() - 2) match
      case s if s == "-n" || s == "-c" => List().asJava
      case _ => List("-n", "-c").filter(_.startsWith(Option(args.lastOrNull()).getOrElse(""))).asJava
