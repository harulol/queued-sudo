package dev.hawu.plugins.queuedsudo.commands

import dev.hawu.plugins.api.dsl.commands.CommandSpec
import dev.hawu.plugins.queuedsudo.I18n.tl
import dev.hawu.plugins.queuedsudo.WorldManager
import org.bukkit.plugin.java.JavaPlugin

class ListCommand(private val plugin: JavaPlugin) : CommandSpec({
    
    permission.set("queued-sudo.list")
    permissionMessage.set("no-permissions".tl("perm" to "queued-sudo.list"))
    
    on command { source, args ->
        val (_, properties) = args.parse()
        
        if(args.isEmpty() || properties.containsKey("-?") || properties.containsKey("--help")) {
            source.tl("help-list", "version" to plugin.description.version)
            return@command true
        }
        
        val count = properties.get("-n").firstOrNull()?.toIntOrNull() ?: properties.get("--count").firstOrNull()?.toIntOrNull() ?: -1
        val suppress = properties.containsKey("--suppress")
        val noUUIDs = properties.containsKey("--no-uuid")
        val showWorlds = properties.containsKey("--worlds")
        
        val map = mutableMapOf<String?, String>()
        
        var displayed = 0
        for(group in WorldManager.getAllGroups()) {
            if(count in 1..displayed) break
            
            source.sendMessage(buildString {
                append("&7Group &b${group.name ?: "&cUnnamed"}&7:\n")
                if(!suppress && group.name in map) append("&c  (DUPLICATED NAME!)\n")
                if(!noUUIDs) append("  &7* UUID: &e${group.uuid}\n")
                append("  &7* Worlds: &a${group.worlds.size}\n")
                if(showWorlds) {
                    group.worlds.forEach { append("    &7- &f$it\n") }
                }
            })
            
            map[group.name] = ""
            displayed++
        }
        
        true
    }
    
    register
    
})