package dev.hawu.plugins.queuedsudo

import dev.hawu.plugins.api.data.YamlFile
import dev.hawu.plugins.api.utils.Strings.color
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.FileNotFoundException

object I18n {
    
    private lateinit var plugin: JavaPlugin
    
    private lateinit var messages: YamlFile
    private lateinit var default: FileConfiguration
    
    fun init(pl: JavaPlugin) {
        plugin = pl
        pl.saveResource("messages.yml", false)
        messages = YamlFile(pl.dataFolder, "messages.yml")
        default = YamlConfiguration.loadConfiguration(pl.getResource("messages.yml").bufferedReader())
    }
    
    fun reload() {
        try {
            messages.reload()
        } catch(fnf: FileNotFoundException) {
            plugin.saveResource("messages.yml", true)
            messages.reload()
        }
    }
    
    @Suppress("UNCHECKED_CAST")
    fun String.tl(vararg placeholders: Pair<String, Any?>): String {
        if(messages[this] == null && default[this] == null) return this
        if(messages[this] == null && default[this] != null) messages[this] = default[this]
        
        var msg = if(messages[this] is List<*>) (messages[this] as List<String>).joinToString("\n") else messages[this].toString()
        placeholders.forEach {
            msg = msg.replace("%${it.first}%", it.second?.toString() ?: "null")
        }
        return msg.color()
    }
    
    fun CommandSender.tl(key: String, vararg placeholders: Pair<String, Any?>) = this.sendMessage(key.tl(*placeholders))
    
}