package dev.hawu.plugins.queuedsudo.commands.groups

import dev.hawu.plugins.api.commands.{AbstractCommandClass, CommandArgument, CommandLine, CommandSource}
import dev.hawu.plugins.api.items.BukkitMaterial
import dev.hawu.plugins.queuedsudo.DataManager.*
import dev.hawu.plugins.queuedsudo.LocalI18n.*
import dev.hawu.plugins.queuedsudo.{DataManager, WorldGroup}
import org.bukkit.{Bukkit, Material}

import java.util
import scala.jdk.CollectionConverters.*

/**
 * The command handler for creating a new world group.
 */
class CreateCommand extends AbstractCommandClass("create") :

   alias("new")
   setPermission("queued-sudo.groups.create")
   allowAny()
   setParser(CommandLine().withArguments("--name", "--icon", "--world").withFlags("-f", "--force"))

   override def run(sender: CommandSource, args: CommandArgument): Unit =
      val (arguments, props) = args.parse().unpack
      if props.containsKey("-?") || props.containsKey("--help") then return sender.tl("help.groups.create")

      val name = Option(props.get("--name")).map(_.get(0)).orNull
      val icon = Option(props.get("--icon")).map(_.get(0)).orNull
      val world = Option(props.get("--world")).getOrElse(new util.ArrayList[String]())
      val force = props.containsKey("-f") || props.containsKey("--force")

      if name == null then return sender.tl("missing-props", "prop" -> "--name")
      if DataManager.existsGroup(name) && !force then return sender.tl("group-exists", "name" -> name)

      val group = new WorldGroup(name)
      DataManager.addGroup(group)
      sender.tl("group-created", "name" -> name)
      if icon != null then
         if Material.getMaterial(icon) == null then return sender.tl("invalid-icon", "icon" -> icon)
         else group.icon = Material.getMaterial(icon)

      for w <- world.asScala do
         if group.worlds.add(w) then sender.tl("added-world", "world" -> w)
         else sender.tl("world-exists", "world" -> w, "name" -> name)

         // Just warn the user that the world did not exist at the time of the command.
         if Bukkit.getWorld(w) == null then sender.tl("world-not-found", "world" -> w)
   end run

   override def tab(sender: CommandSource, args: CommandArgument): util.List[String] = args.get(args.size() - 2) match
      case "--name" => List().asJava
      case "--world" => Bukkit.getWorlds.asScala
         .map(w => if w.getName.split(" ").length > 1 then s"\"${w.getName}\"" else w.getName)
         .filter(args.getUnderlyingList.contains(_))
         .asJava
      case "--icon" => BukkitMaterial.getDisplayableMaterials.asScala.map(_.name).asJava
      case _ => List("--name", "--world", "--icon", "-f", "--force")
         .filter(arg => !arg.equalsIgnoreCase("--world") && !args.getUnderlyingList.contains(arg) && arg.startsWith(Option(args.lastOrNull()).getOrElse("")))
         .asJava
   end tab
