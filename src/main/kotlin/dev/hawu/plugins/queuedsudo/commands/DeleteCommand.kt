package dev.hawu.plugins.queuedsudo.commands

import dev.hawu.plugins.api.dsl.commands.CommandSpec
import dev.hawu.plugins.api.utils.Strings.toUUIDOrNull
import dev.hawu.plugins.queuedsudo.I18n.tl
import dev.hawu.plugins.queuedsudo.WorldManager
import org.bukkit.plugin.java.JavaPlugin

class DeleteCommand(private val pl: JavaPlugin) : CommandSpec({
    
    permission.set("queued-sudo.delete")
    permissionMessage.set("no-permissions".tl("perm" to "queued-sudo.delete"))
    
    on command { source, args ->
        val (arguments, properties) = args.parse()
        
        if(args.isEmpty() || properties.containsKey("-?") || properties.containsKey("--help")) {
            source.tl("help-delete", "version" to pl.description.version)
            return@command true
        }
        
        val group = arguments.joinToString(" ")
        val ignoreMultiple = properties.containsKey("--ignore-multiples")
        
        val list = when {
            properties.containsKey("--uuid") -> group.toUUIDOrNull()?.let { WorldManager.lookupGroup(it) }?.let { listOf(it) } ?: emptyList()
            properties.containsKey("--world") -> WorldManager.lookupGroupsByWorld(group, !properties.containsKey("-c"))
            else -> WorldManager.lookupGroupsByName(group, !properties.containsKey("-c"))
        }
        
        when {
            list.isEmpty() -> source.tl("no-groups-found")
            list.size > 1 && !ignoreMultiple -> source.tl("multiple-groups")
            else -> {
                list.indices.forEach { num ->
                    val it = list[num]
                    source.tl("group-deleted", "name" to (it.name ?: "&cUnnamed"), "uuid" to it.uuid.toString())
                    WorldManager.deleteGroup(it)
                }
            }
        }
        
        true
    }
    
    register
    
})