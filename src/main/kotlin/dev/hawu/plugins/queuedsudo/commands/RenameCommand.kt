package dev.hawu.plugins.queuedsudo.commands

import dev.hawu.plugins.api.dsl.commands.CommandSpec
import dev.hawu.plugins.api.utils.Strings.toUUID
import dev.hawu.plugins.api.utils.Strings.toUUIDOrNull
import dev.hawu.plugins.queuedsudo.I18n.tl
import dev.hawu.plugins.queuedsudo.WorldGroup
import dev.hawu.plugins.queuedsudo.WorldManager
import org.bukkit.plugin.java.JavaPlugin

class RenameCommand(private val pl: JavaPlugin) : CommandSpec({
    
    permission.set("queued-sudo.rename")
    permissionMessage.set("no-permissions".tl("perm" to "queued-sudo.rename"))
    
    on command { source, args ->
        val (_, properties) = args.parse()
        
        if(args.isEmpty() || properties.containsKey("-?") || properties.containsKey("--help")) {
            source.tl("help-rename", "version" to pl.description.version)
            return@command true
        }
        
        val group = properties.get("-g").firstOrNull() ?: properties.get("--group").firstOrNull()
        val newName = properties.get("-n").firstOrNull() ?: properties.get("--name").firstOrNull()
    
        val list: List<WorldGroup> = when {
            properties.containsKey("--uuid") -> group?.toUUIDOrNull()?.let { WorldManager.lookupGroup(it) }?.let { listOf(it) } ?: emptyList()
            properties.containsKey("--world") -> WorldManager.lookupGroupsByWorld(group, !properties.containsKey("-c"))
            else -> WorldManager.lookupGroupsByName(group, !properties.containsKey("-c"))
        }
        
        when {
            list.isEmpty() -> source.tl("no-groups-found")
            list.size > 1 -> source.tl("multiple-groups")
            else -> {
                source.tl("group-renamed", "old" to (list[0].name ?: "&cUnnamed"), "new" to (newName ?: "&cUnnamed"))
                WorldManager.renameGroup(list[0], newName)
            }
        }
        
        true
    }
    
    register
    
})