package dev.hawu.plugins.queuedsudo

import com.google.common.collect.ArrayListMultimap
import dev.hawu.plugins.api.data.YamlFile
import dev.hawu.plugins.queuedsudo.I18n.tl
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

object WorldManager {
    
    private lateinit var plugin: JavaPlugin
    private val groups = mutableMapOf<UUID, WorldGroup>()
    
    // Maps for fast lookups using names and worlds names, case-insensitive.
    private val groupNamesLookup = ArrayListMultimap.create<String, WorldGroup>()
    private val worldsLookup = ArrayListMultimap.create<String, WorldGroup>()
    
    // Maps for fast lookups using names and worlds names, but case-sensitive.
    private val groupNames = ArrayListMultimap.create<String, WorldGroup>()
    private val worlds = ArrayListMultimap.create<String, WorldGroup>()
    
    @Suppress("UNCHECKED_CAST")
    fun init(pl: JavaPlugin) {
        plugin = pl
        pl.dataFolder.mkdirs()
        
        val file = YamlFile(pl.dataFolder, "groups.yml")
        if(file["groups"] == null || file["groups"] !is List<*>) return
        
        try {
            (file["groups"] as List<*>).forEach {
                val group = it as WorldGroup
                groups[group.uuid] = group
                groupNamesLookup.put(group.name?.lowercase(), group)
                groupNames.put(group.name, group)
                group.worlds.forEach { w ->
                    worldsLookup.put(w.lowercase(), group)
                    worlds.put(w, group)
                }
            }
        } catch(ignored: ClassCastException) {}
    }
    
    fun save() {
        val file = YamlFile(plugin.dataFolder, "groups.yml")
        file["groups"] = groups.values.toList()
        file.save()
    }
    
    fun clear() {
        save()
        groupNames.clear()
        worldsLookup.clear()
        groupNames.clear()
        worlds.clear()
    }
    
    fun createNewEmptyGroup() = WorldGroup(generateUUID())
    
    fun createNewGroup(): WorldGroup {
        val group = WorldGroup(generateUUID())
        groups[group.uuid] = group
        groupNamesLookup.put(null, group)
        groupNames.put(null, group)
        return group
    }
    
    fun addGroup(group: WorldGroup) {
        groups[group.uuid] = group
        groupNamesLookup.put(group.name?.lowercase(), group)
        groupNames.put(group.name, group)
        group.worlds.forEach {
            worldsLookup.put(it.lowercase(), group)
            worlds.put(it, group)
        }
    }
    
    fun addWorld(worldName: String, group: WorldGroup) {
        group.worlds.add(worldName)
        worldsLookup.put(worldName.lowercase(), group)
        worlds.put(worldName, group)
    }
    
    fun removeWorld(worldName: String, group: WorldGroup) {
        group.worlds.remove(worldName)
        worldsLookup.remove(worldName.lowercase(), group)
        worlds.remove(worldName, group)
    }
    
    fun renameGroup(group: WorldGroup, newName: String?) {
        groupNamesLookup.remove(group.name?.lowercase(), group)
        groupNamesLookup.put(newName?.lowercase(), group)
        groupNames.remove(group.name, group)
        groupNames.put(newName, group)
        
        group.name = newName
    }
    
    fun deleteGroup(group: WorldGroup) {
        groupNamesLookup.remove(group.name?.lowercase(), group)
        groupNames.remove(group.name, group)
        group.worlds.forEach {
            worldsLookup.remove(it.lowercase(), group)
            worlds.remove(it, group)
        }
        
        groups.remove(group.uuid)
    }
    
    fun getAllGroups() = groups.values
    
    fun lookupGroups(query: String?, ignoresCase: Boolean, byName: Boolean, byWorld: Boolean): List<WorldGroup> = when {
        byName -> lookupGroupsByName(query, ignoresCase)
        byWorld -> lookupGroupsByWorld(query, ignoresCase)
        else -> lookupGroupsByName(query, ignoresCase).ifEmpty { lookupGroupsByWorld(query, ignoresCase) }
    }
    
    fun lookupGroupsByName(name: String?, ignoresCase: Boolean): List<WorldGroup> = if(ignoresCase) groupNamesLookup.get(name?.lowercase()).toList() else groupNames.get(name).toList()
    fun lookupGroupsByWorld(worldName: String?, ignoresCase: Boolean): List<WorldGroup> = if(ignoresCase) worldsLookup.get(worldName?.lowercase()).toList() else worlds.get(worldName).toList()
    fun lookupGroup(uuid: UUID) = groups[uuid]
    
    // Generate a random UUID that has not been used for a group, even though the likelihood is very low.
    private fun generateUUID(): UUID = UUID.randomUUID().run {
        if(groups[this] != null) {
            Bukkit.getConsoleSender().tl("duplicate-uuid")
            generateUUID()
        } else this
    }
    
}