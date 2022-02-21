package dev.hawu.plugins.queuedsudo.gui

import dev.hawu.plugins.api.Tasks
import dev.hawu.plugins.api.events.EventSubscriptionBuilder
import dev.hawu.plugins.api.gui.pagination.GuiPaginationBuilder
import dev.hawu.plugins.api.gui.{GuiClickEvents, GuiElement, GuiModel}
import dev.hawu.plugins.api.items.{BukkitMaterial, ItemStackBuilder}
import dev.hawu.plugins.queuedsudo.LocalI18n.*
import dev.hawu.plugins.queuedsudo.{DataManager, LocalI18n, QueuedSudo, WorldGroup}
import org.bukkit.World.Environment
import org.bukkit.entity.{HumanEntity, Player}
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.*

import java.text.DecimalFormat
import java.util
import java.util.UUID
import java.util.concurrent.CompletableFuture
import scala.::
import scala.jdk.CollectionConverters.*
import scala.runtime.Nothing$
import scala.util.Random

/**
 * Holds the main gui of the plugin.
 */
object MainGui:

   /**
    * Opens the main menu.
    *
    * @param player the player to open the menu for
    */
   def main(player: Player): Unit =
      val model = GuiModel(27, s"QueuedSudo v${QueuedSudo.version}")

      model.mount(11, new GuiElement {
         override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            if permissionGuard(player, model, 15, "queued-sudo.reload") then sudo(player)

         override def render(): ItemStack = ItemStackBuilder.of(Material.EXPLOSIVE_MINECART)
            .name("&e&lSudo")
            .lore(
               "&7Create a /sudo and queue it",
               "&7for a player.",
            )
            .build()
      })

      model.mount(13, new GuiElement {
         override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            if permissionGuard(player, model, 15, "queued-sudo.reload") then groups(player)

         override def render(): ItemStack = ItemStackBuilder.of(Material.CHEST)
            .name("&e&lGroups")
            .lore(
               "&7Groups of worlds for queuing",
               "&7commands in.",
            )
            .build()
      })

      model.mount(15, new GuiElement {
         override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            if permissionGuard(player, model, 15, "queued-sudo.reload") then
               player.closeInventory()
               player.tl("reloaded")
               LocalI18n.reload()

         override def render(): ItemStack = ItemStackBuilder.of(Material.COMMAND)
            .name("&e&lReload")
            .lore(
               "&7Reloads this plugin's messages",
               "&7and configuration files.",
            )
            .build()
      })

      GuiManager.addCloseButton(model, 22)
      model.open(player)
   end main

   /**
    * Opens the menu to allow the user to sudo.
    *
    * @param player the player to open the menu for
    */
   def sudo(player: Player): Unit =
      val model = GuiModel(27, "Executable Creation")

      // The instance of the executable (default 0, await 1 or repeating 2)
      val typeElement = new GuiElement[Int](0) {
         override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            setState(getNewState(getState, event))

         private def getLore: List[String] = this.getState match
            case 0 => List(
               "&7An executable that is executed &cright",
               "&caway &7as the target joins the specified",
               "&7world group.",
               "",
               "&fSelected: &aDefault",
               "",
               "&6Left click &eto cycle forwards",
               "&6Right click &eto cycle backwards",
            )
            case 1 => List(
               "&7An executable that &cwaits&7 a moment after",
               "&7the target joins the group before",
               "&7being executed.",
               "",
               "&fSelected: &aAwait",
               "",
               "&6Left click &eto cycle forwards",
               "&6Right click &eto cycle backwards",
            )
            case 2 => List(
               "&7An executable that &cwaits&7 a moment before",
               "&7executing &cmultiple times&7 with &cintervals&7",
               "&7between executions.",
               "",
               "&fSelected: &aRepeating",
               "",
               "&6Left click &eto cycle forwards",
               "&6Right click &eto cycle backwards",
            )
            case _ => throw new IllegalStateException("Unknown state")

         override def render(): ItemStack = ItemStackBuilder.of(Material.MINECART)
            .name("&e&lExecutable Type")
            .lore(getLore: _*)
            .build()
      }
      model.mount(11, typeElement)

      // The flags of the executable (self 0, op 1 or console 2)
      val flagElement = new GuiElement[Int](0) {
         override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            setState(getNewState(getState, event))

         private def getLore: List[String] = this.getState match
            case 0 => List(
               "&7The embedded command will be performed",
               "&7by the &cplayer itself&7.",
               "",
               "&fSelected: &aSelf",
               "",
               "&6Left click &eto cycle forwards",
               "&6Right click &eto cycle backwards",
            )
            case 1 => List(
               "&7The embedded command will be performed",
               "&7by the &aplayer as an operator&7.",
               "",
               "&fSelected: &aOP",
               "",
               "&6Left click &eto cycle forwards",
               "&6Right click &eto cycle backwards",
            )
            case 2 => List(
               "&7The embedded command will be performed",
               "&7by the &cconsole&7.",
               "",
               "&fSelected: &aConsole",
               "",
               "&6Left click &eto cycle forwards",
               "&6Right click &eto cycle backwards",
            )
            case _ => throw new IllegalStateException("Unknown state")

         override def render(): ItemStack = ItemStackBuilder.of(Material.PAPER)
            .name("&e&lExecutable Flag")
            .lore(getLore: _*)
            .build()
      }
      model.mount(12, flagElement)

      // The player to execute the command as
      val playerElement = new GuiElement[UUID] {
         private def getName: String = this.getState match
            case null => "&cNone"
            case _ => s"&a${Bukkit.getOfflinePlayer(getState).getName}"

         override def render(): ItemStack = ItemStackBuilder.of(Material.SKULL_ITEM)
            .durability(3)
            .name("&e&lPlayer")
            .lore(
               "&7The player to be executed as.",
               "",
               s"&fCurrently selected: $getName",
               "",
               "&6Click &eto choose",
            )
            .build()
      }
      model.mount(14, playerElement)

      val groupElement = new GuiElement[String] {
         override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            openGroupsPagination(event.getWhoClicked, this, model)

         private def getName: String = this.getState match
            case null => "&cNone"
            case _ => s"&a$getState"

         override def render(): ItemStack = ItemStackBuilder.of(Material.ENDER_CHEST)
            .name("&e&lWorld Group")
            .lore(
               "&7The group of worlds the player",
               "&7has to be in for the executable",
               "&7to run.",
               "",
               s"&fCurrently selected: $getName",
               "",
               "&6Click &eto choose",
            )
            .build()
      }
      model.mount(15, groupElement)

      GuiManager.addCloseButton(model, 22)
      model.open(player)
   end sudo

   private def getNewState(state: Int, event: InventoryClickEvent, min: Int = 0, max: Int = 2): Int =
      val newState = if event.isLeftClick then state + 1 else if event.isRightClick then state - 1 else state
      if newState < min then max else if newState > max then min else newState

   private def openGroupsPagination(player: HumanEntity, element: GuiElement[String], model: GuiModel): Unit =
      new GuiPaginationBuilder[WorldGroup]
         .setModelSupplier(() => GuiManager.closableModel(54, "Choose a group", 49))
         .setCollection(DataManager.getGroups.asJavaCollection)
         .setItemGenerator((group, _) => new GuiElement {
            override def handleClick(event: InventoryClickEvent): Unit =
               event.setCancelled(true)
               element.setState(group.name)
               model.open(player)

            override def render(): ItemStack = ItemStackBuilder.of(group.icon)
               .name(s"&e&l${group.name}")
               .lore(" ", "&6Click &eto choose")
               .build()
         })
         .build(player)
   end openGroupsPagination

   /**
    * Opens the menu to allow the user to modify, create
    * or delete world groups.
    *
    * @param player the player to open the menu for
    */
   def groups(player: Player): Unit = new GuiPaginationBuilder[WorldGroup]
      .setModelSupplier(() => GuiManager.closableModel(54, "World Groups", 49))
      .setItemGenerator((group, _) => new GuiElement {
         override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            if event.isRightClick then
               swap(new GroupDeletionButton(group, this))
            else if event.isLeftClick then
               modifyGroup(player, group)

         override def render(): ItemStack = ItemStackBuilder.of(group.icon)
            .name(s"&e&l${group.name}")
            .lore(
               "",
               s"&7Worlds: &a${group.worlds.size}",
               "",
               "&6Left click &eto edit",
               "&6Right click &eto delete",
            )
            .build()
      })
      .setCollection(DataManager.getGroups.asJavaCollection)
      .build(player)
   end groups

   private def modifyGroup(player: Player, group: WorldGroup): Unit =
      val model = GuiManager.closableModel(27, "Group Modification", 22)

      // The button to change the group's name.
      model.mount(11, new GuiElement {
         override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            player.tl("group-rename-prompt")
            GuiClickEvents.requestTextInput(player, s => {
               if DataManager.existsGroup(s) then
                  player.tl("group-exists", "name" -> s)
               else
                  val old = group.name
                  group.name = s
                  player.tl("group-renamed", "old" -> old, "new" -> s)
                  modifyGroup(player, group)
            })

         override def render(): ItemStack = ItemStackBuilder.of(Material.NAME_TAG)
            .name("&e&lName")
            .lore(
               "&7The identifier of the group.",
               "",
               s"&fCurrently &a${group.name}",
               "",
               "&6Click &eto change",
            )
            .build()
      })

      // The button to change the group's icon.
      model.mount(13, new GuiElement {
         override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            if event.isRightClick then
               group.icon = BukkitMaterial.getDisplayableMaterials.get(Random.nextInt(BukkitMaterial.getDisplayableMaterials.size))
               setState(null)
            else if event.isLeftClick then new GuiPaginationBuilder[Material]
               .setModelSupplier(() => GuiManager.closableModel(54, "Choose an icon", 49))
               .setPredicate((mat, filter) => mat.name().replace("_", " ").toLowerCase.contains(filter.toLowerCase))
               .setItemGenerator((mat, _) => new GuiElement {
                  override def handleClick(event: InventoryClickEvent): Unit =
                     event.setCancelled(true)
                     group.icon = mat
                     modifyGroup(player, group)

                  override def render(): ItemStack = ItemStackBuilder.of(mat)
                     .name(s"&e&l${mat.name.split("_").map(_.capitalize).mkString(" ")}")
                     .lore(" ", "&6Click &eto choose")
                     .build()
               })
               .setCollection(BukkitMaterial.getDisplayableMaterials)
               .build(player)
         end handleClick

         override def render(): ItemStack = ItemStackBuilder.of(group.icon)
            .name("&e&lDisplay Icon")
            .lore(
               "&7The icon for the group to",
               "&7be displayed as.",
               "",
               "&6Left click &eto change",
               "&6Right click &eto randomize",
            )
            .build()
      })

      // The button to modify the group's worlds.
      model.mount(15, new GuiElement {
         private def buildPreview: Seq[String] =
            var worlds = Seq("&7The worlds within this group.", "", s"&fWorlds: &a${group.worlds.size}") ++ group.worlds.map(w => s"&7- &a$w")
            if worlds.size > 5 then worlds = worlds.drop(5)
            (worlds ++ Seq(s"&8and ${worlds.size - 5} more...", " ", "&6Click &eto modify", "&6Right click &eto clear"))

         override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            openGroupWorlds(player, group)

         override def render(): ItemStack = ItemStackBuilder.of(Material.GRASS)
            .name("&e&lWorlds")
            .lore(buildPreview: _*)
            .build()
      })

      model.open(player)
   end modifyGroup

   private def permissionGuard(player: Player, model: GuiModel, slot: Int, permission: String): Boolean =
      if !player.hasPermission(permission) then
         val oldButton = model.getElement(slot)
         model.mount(slot, new GuiElement {
            Tasks.scheduleLater(QueuedSudo.instance, 60, runnable => swap(oldButton))

            override def handleClick(event: InventoryClickEvent): Unit =
               event.setCancelled(true)

            override def render(): ItemStack = ItemStackBuilder.of(Material.REDSTONE_BLOCK)
               .name("&c&lNo permission")
               .lore(
                  "&7You don't have the permission",
                  "&7to use this feature.",
               )
               .build()
         })
         false
      else true
   end permissionGuard

   private def openSudoTargetsPagination(player: HumanEntity, element: GuiElement[UUID], model: GuiModel): Unit =
      new GuiPaginationBuilder[OfflinePlayer]
         .setModelSupplier(() => GuiManager.closableModel(54, "Choose a target", 49))
         .setCollection(Bukkit.getOfflinePlayers.filter(p => p.hasPlayedBefore && p.getName != null && p.getUniqueId != player.getUniqueId && !p.isOp).toList.asJava)
         .setItemGenerator((op, _) => new GuiElement {
            override def handleClick(event: InventoryClickEvent): Unit =
               event.setCancelled(true)
               element.setState(op.getUniqueId)
               model.open(player)

            override def render(): ItemStack = ItemStackBuilder.of(Material.SKULL_ITEM)
               .durability(3)
               .name(s"&e&l${op.getName}")
               .lore(" ", "&6Click &eto choose")
               .transform(meta => meta.asInstanceOf[SkullMeta].setOwner(op.getName))
               .build()
         })
         .setPredicate((op, filter) => op.getName.toLowerCase.contains(filter.toLowerCase))
         .build(player)
   end openSudoTargetsPagination

   private def openGroupWorlds(player: Player, group: WorldGroup): Unit =
      new GuiPaginationBuilder[String]
         .setModelSupplier(() => {
            val model = GuiManager.closableModel(54, "Worlds", 49)
            model.mount(4, new GuiElement {
               override def handleClick(event: InventoryClickEvent): Unit =
                  event.setCancelled(true)
                  new GuiPaginationBuilder[World]()
                     .setModelSupplier(() => GuiManager.closableModel(54, "Choose a world", 49))
                     .setItemGenerator((world, _) => new GuiElement {
                        private def getMaterial: Material = world.getEnvironment match
                           case Environment.NETHER => Material.NETHERRACK
                           case Environment.THE_END => Material.ENDER_STONE
                           case _ => Material.GRASS

                        override def handleClick(event: InventoryClickEvent): Unit =
                           event.setCancelled(true)
                           group.worlds.add(world.getName)
                           openGroupWorlds(player, group)

                        override def render(): ItemStack = ItemStackBuilder.of(getMaterial)
                           .name(s"&e&l${world.getName}")
                           .lore(" ", "&6Click &eto add")
                           .build()
                     })
                     .setPredicate((world, filter) => world.getName.toLowerCase.contains(filter.toLowerCase))
                     .setBackAction(_ => openGroupWorlds(player, group))
                     .setCollection(Bukkit.getWorlds)
                     .build(player)

               override def render(): ItemStack = ItemStackBuilder.of(Material.EYE_OF_ENDER)
                  .name("&e&lAdd")
                  .lore("&7Adds world to this group.")
                  .build()
            })
            model
         })
         .setBackAction(_ => modifyGroup(player, group))
         .setPredicate((world, filter) => world.toLowerCase.contains(filter.toLowerCase))
         .setItemGenerator((world, _) => new GuiElement {
            private var size: Option[Double] = None
            private var calculated = false
            private val formatter = DecimalFormat("#,###.##")

            Tasks.scheduleAsync(QueuedSudo.instance, runnable => {
               val size = Option(Bukkit.getWorld(world)).map(_.getWorldFolder.getTotalSpace).getOrElse(0L)
               calculated = true
               this.size = Some(size / 1024.0 / 1024.0)
               Tasks.schedule(QueuedSudo.instance, runnable => {
                  setState(null)
               })
            })

            private def getWorldSize: String =
               if calculated && size.isEmpty then "&cUnknown"
               else if size.isEmpty then "&eCalculating..."
               else s"&a${formatter.format(size.get)} MB"

            private def getMaterial: Material = Option(Bukkit.getWorld(world)).map(w => w.getEnvironment) match
               case None => Material.BEDROCK
               case Some(Environment.NETHER) => Material.NETHERRACK
               case Some(Environment.THE_END) => Material.ENDER_STONE
               case Some(Environment.NORMAL) => Material.GRASS
               case _ => Material.REDSTONE_BLOCK

            override def handleClick(event: InventoryClickEvent): Unit =
               event.setCancelled(true)
               group.worlds.remove(world)
               openGroupWorlds(player, group)

            override def render(): ItemStack = ItemStackBuilder.of(getMaterial)
               .name(s"&e&l$world")
               .lore(
                  "",
                  s"&fEnvironment: ${Option(Bukkit.getWorld(world)).map(w => w.getEnvironment).getOrElse("&cUnknown")}",
                  s"&fWorld Size: $getWorldSize",
                  "",
                  "&6Click &eto remove",
               )
               .build()
         })
         .setCollection(group.worlds.asJava)
         .build(player)
   end openGroupWorlds

   private class GroupDeletionButton(private val group: WorldGroup, private val to: GuiElement[_]) extends GuiElement[Int](5) {
      private val task = Tasks.scheduleTimer(QueuedSudo.instance, 20, 20, runnable => {
         this.setState(this.getState - 1)
         if this.getState < 0 then swap(to)
      })

      override def handleClick(event: InventoryClickEvent): Unit =
         event.setCancelled(true)

      override def render(): ItemStack = ItemStackBuilder.of(Material.STAINED_GLASS_PANE)
         .name("&c&lDeletion Confirmation")
         .lore(
            "&7Please confirm that you want",
            "&7to delete this world group",
            "&7by shift clicking.",
            "",
            s"&7Groups count: &a${group.worlds.size}",
            "",
            s"&7Cancelling in &c${this.getState}s&7."
         )
         .build()
   }
