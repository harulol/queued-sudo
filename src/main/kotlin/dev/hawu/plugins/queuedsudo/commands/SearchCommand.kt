package dev.hawu.plugins.queuedsudo.commands

import dev.hawu.plugins.api.dsl.commands.CommandSpec
import dev.hawu.plugins.api.utils.Strings.toUUIDOrNull
import dev.hawu.plugins.queuedsudo.I18n.tl
import dev.hawu.plugins.queuedsudo.WorldGroup
import dev.hawu.plugins.queuedsudo.WorldManager
import org.bukkit.plugin.java.JavaPlugin

class SearchCommand(private val pl: JavaPlugin) : CommandSpec({
    
    permission.set("queued-sudo.search")
    permissionMessage.set("no-permissions".tl("perm" to "queued-sudo.search"))
    
    on command { source, args ->
        val (arguments, properties) = args.parse()
        
        if(args.isEmpty() || properties.containsKey("-?") || properties.containsKey("--help")) {
            source.tl("help-search", "version" to pl.description.version)
            return@command true
        }
        
        val query = properties.get("-q").firstOrNull() ?: properties.get("--query").firstOrNull() ?: arguments.joinToString(" ")
        val count = properties.get("-n").firstOrNull()?.toIntOrNull() ?: properties.get("--count").firstOrNull()?.toIntOrNull() ?: -1
        
        val list: List<WorldGroup>
        
        when {
            properties.containsKey("--uuid") -> {
                val uuid = query.toUUIDOrNull()
                if(uuid == null) {
                    source.tl("invalid-uuid")
                    return@command true
                }
                
                list = WorldManager.lookupGroup(uuid)?.run { listOf(this) } ?: emptyList()
            }
            properties.containsKey("--world") -> list = WorldManager.lookupGroupsByWorld(query, !properties.containsKey("-c"))
            else -> list = WorldManager.lookupGroupsByName(query, !properties.containsKey("-c"))
        }
        
        if(list.isEmpty()) {
            source.tl("no-groups-found")
            return@command true
        }
        
        var displayed = 0
        for(group in list) {
            if(count in 1..displayed) break
            source.tl(
                "group-found",
                "name" to (group.name ?: "&cUnnamed"),
                "uuid" to group.uuid.toString(),
                "worlds_count" to group.worlds.size,
                "executables_count" to group.executables.values.sumOf { it.size },
                "players_count" to group.executables.size,
            )
            displayed++
        }
        
        true
    }
    
    register
    
})