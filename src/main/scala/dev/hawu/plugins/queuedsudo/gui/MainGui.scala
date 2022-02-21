package dev.hawu.plugins.queuedsudo.gui

import dev.hawu.plugins.api.Tasks
import dev.hawu.plugins.api.events.EventSubscriptionBuilder
import dev.hawu.plugins.api.gui.pagination.GuiPaginationBuilder
import dev.hawu.plugins.api.gui.{GuiElement, GuiModel}
import dev.hawu.plugins.api.items.ItemStackBuilder
import dev.hawu.plugins.queuedsudo.LocalI18n.*
import dev.hawu.plugins.queuedsudo.{DataManager, LocalI18n, QueuedSudo, WorldGroup}
import org.bukkit.entity.{HumanEntity, Player}
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.{Bukkit, DyeColor, Material, OfflinePlayer}

import java.util
import java.util.UUID
import scala.jdk.CollectionConverters.*

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

   private def modifyGroup(player: Player, group: WorldGroup): Unit = ???

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
