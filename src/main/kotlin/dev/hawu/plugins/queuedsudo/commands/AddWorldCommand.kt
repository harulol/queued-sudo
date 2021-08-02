package dev.hawu.plugins.queuedsudo.commands

import dev.hawu.plugins.api.dsl.commands.CommandSpec
import dev.hawu.plugins.api.utils.Strings.toUUIDOrNull
import dev.hawu.plugins.queuedsudo.I18n.tl
import dev.hawu.plugins.queuedsudo.WorldManager
import org.bukkit.plugin.java.JavaPlugin

class AddWorldCommand(private val pl: JavaPlugin) : CommandSpec({
    
    permission.set("queued-sudo.add-world")
    permissionMessage.set("no-permissions".tl("perm" to "queued-sudo.add-world"))
    
    on command { source, args ->
        val (_, properties) = args.parse()
        
        if(args.isEmpty() || properties.containsKey("-?") || properties.containsKey("--help")) {
            source.tl("help-add-world", "version" to pl.description.version)
            return@command true
        }
        
        val group = properties.get("-g").firstOrNull() ?: properties.get("--group").firstOrNull()
        val worlds = properties.get("-w").apply { addAll(properties.get("--world")) }
        
        val list = when {
            properties.containsKey("--uuid") -> group?.toUUIDOrNull()?.let { WorldManager.lookupGroup(it) }?.let { listOf(it) } ?: emptyList()
            properties.containsKey("--world") -> WorldManager.lookupGroupsByWorld(group, !properties.containsKey("-c"))
            else -> WorldManager.lookupGroupsByName(group, !properties.containsKey("-c"))
        }
        
        when {
            list.isEmpty() -> source.tl("no-groups-found")
            list.size > 1 -> source.tl("multiple-groups")
            else -> {
                val g = list[0]
                worlds.forEach {
                    if(it in g.worlds) source.tl("world-already-added", "group" to (g.name ?: "&cUnnamed"), "world" to it)
                    else {
                        source.tl("world-added", "group" to (g.name ?: "&cUnnamed"), "world" to it)
                        WorldManager.addWorld(it, g)
                    }
                }
            }
        }
        
        true
    }
    
    register
    
})