package dev.hawu.plugins.queuedsudo

import dev.hawu.plugins.queuedsudo.commands.BaseCommand
import dev.hawu.plugins.queuedsudo.executables.*
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPluginLoader
import java.io.File

class QueuedSudo : JavaPlugin() {
    
    override fun onEnable() {
        ConfigurationSerialization.registerClass(WorldGroup::class.java)
        ConfigurationSerialization.registerClass(AwaitExecutable::class.java)
        ConfigurationSerialization.registerClass(DefaultExecutable::class.java)
        ConfigurationSerialization.registerClass(RepeatingExecutable::class.java)
        ConfigurationSerialization.registerClass(TimedExecutable::class.java)
        
        I18n.init(this)
        WorldManager.init(this)
        BaseCommand(this)
    }
    
    override fun onDisable() {
        WorldManager.clear()
    }

}