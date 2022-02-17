package dev.hawu.plugins.queuedsudo

import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.event.player.{PlayerChangedWorldEvent, PlayerJoinEvent}

/**
 * The main listener of the plugin.
 */
object MainListener extends Listener:
   
   @EventHandler
   private def onWorldChange(event: PlayerChangedWorldEvent): Unit = DataManager.getUser(event.getPlayer).get.run()
      
   @EventHandler
   private def onJoin(event: PlayerJoinEvent): Unit = DataManager.getUser(event.getPlayer).get.run()
      