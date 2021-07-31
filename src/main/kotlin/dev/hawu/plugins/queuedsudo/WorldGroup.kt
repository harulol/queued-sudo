package dev.hawu.plugins.queuedsudo

import dev.hawu.plugins.api.utils.Strings.toUUID
import dev.hawu.plugins.queuedsudo.executables.Executable
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player
import java.util.*

class WorldGroup(
    val uuid: UUID,
    var name: String? = null,
    val worlds: MutableList<String> = mutableListOf(),
    val executables: MutableMap<UUID, MutableList<Executable>> = mutableMapOf(),
) : ConfigurationSerializable {
    
    fun queueExecutable(player: OfflinePlayer, executable: Executable) {
        if(player.isOnline && player.player.world.name in worlds) return executable.run(player.uniqueId)
        executables.putIfAbsent(player.uniqueId, mutableListOf())
        executables[player.uniqueId]!!.add(executable)
    }
    
    fun runAllExecutables(player: Player) {
        val list = executables[player.uniqueId] ?: return
        list.forEach {
            it.run(player.uniqueId)
        }
        executables.remove(player.uniqueId)
    }
    
    override fun serialize() = mapOf(
        "uuid" to uuid.toString(),
        "name" to name,
        "worlds" to worlds,
        "executables" to executables.mapKeys { it.key.toString() },
    )
    
    override fun equals(other: Any?) = other is WorldGroup && other.uuid == uuid
    override fun toString() = "WorldGroup{uuid=$uuid}"
    override fun hashCode() = uuid.hashCode()
    
    companion object {
        
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun deserialize(map: Map<String, Any>) = WorldGroup(
            uuid = map["uuid"].toString().toUUID(),
            name = map["name"]?.toString(),
            worlds = map["worlds"] as MutableList<String>,
            executables = map["executables"] as MutableMap<UUID, MutableList<Executable>>,
        )
        
    }
    
}