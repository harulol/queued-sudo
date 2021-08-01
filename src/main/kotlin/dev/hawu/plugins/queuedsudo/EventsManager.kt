package dev.hawu.plugins.queuedsudo

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent

object EventsManager : Listener {
    
    @EventHandler
    fun onWorldChange(event: PlayerChangedWorldEvent) {
        val groups = WorldManager.lookupGroupsByWorld(event.player.world.name, false)
        groups.forEach { it.runAllExecutables(event.player) }
    }
    
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val groups = WorldManager.lookupGroupsByWorld(event.player.world.name, false)
        groups.forEach { it.runAllExecutables(event.player) }
    }
    
}