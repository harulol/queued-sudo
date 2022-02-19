package dev.hawu.plugins.queuedsudo.gui

import dev.hawu.plugins.api.gui.{GuiElement, GuiModel}
import dev.hawu.plugins.api.items.ItemStackBuilder
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * Common utility methods for all GUIs.
 */
object GuiManager:

   /**
    * Adds a close button to a model at the provided slot.
    *
    * @param model the model to add to
    * @param slot  the slot to place at
    */
   def addCloseButton(model: GuiModel, slot: Int): Unit =
      model.mount(slot, new GuiElement[Unit] {
         override def handleClick(event: InventoryClickEvent): Unit =
            event.setCancelled(true)
            event.getWhoClicked.closeInventory()

         override def render(): ItemStack = ItemStackBuilder.of(Material.BARRIER)
            .name("&c&lClose")
            .lore("&7Closes the menu")
            .build()
      })
   end addCloseButton

   /**
    * Creates a model with a close button.
    *
    * @param size      the size of the model
    * @param name      the name of the model
    * @param closeSlot the slot to place the close button at
    * @return the model
    */
   def closableModel(size: Int, name: String, closeSlot: Int): GuiModel =
      val model = GuiModel(size, name)
      addCloseButton(model, closeSlot)
      model
