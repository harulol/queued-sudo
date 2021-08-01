package dev.hawu.plugins.queuedsudo

import dev.hawu.plugins.queuedsudo.commands.BaseCommand
import dev.hawu.plugins.queuedsudo.executables.*
import org.bukkit.Bukkit
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.plugin.java.JavaPlugin

class QueuedSudo : JavaPlugin() {
    
    override fun onEnable() {
        ConfigurationSerialization.registerClass(WorldGroup::class.java)
        ConfigurationSerialization.registerClass(AwaitExecutable::class.java)
        ConfigurationSerialization.registerClass(DefaultExecutable::class.java)
        ConfigurationSerialization.registerClass(RepeatingExecutable::class.java)
        
        I18n.init(this)
        WorldManager.init(this)
        BaseCommand(this)
        server.pluginManager.registerEvents(EventsManager, this)
    }
    
    override fun onDisable() {
        WorldManager.clear()
        Bukkit.getScheduler().cancelTasks(this)
    }

}