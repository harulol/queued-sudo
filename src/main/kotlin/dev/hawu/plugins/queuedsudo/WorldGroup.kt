package dev.hawu.plugins.queuedsudo

import dev.hawu.plugins.api.utils.Strings.toUUID
import dev.hawu.plugins.queuedsudo.executables.Executable
import org.bukkit.configuration.serialization.ConfigurationSerializable
import java.util.*

class WorldGroup(
    val uuid: UUID,
    var name: String? = null,
    val worlds: MutableList<String> = mutableListOf(),
    val executables: MutableList<Executable> = mutableListOf(),
) : ConfigurationSerializable {
    
    override fun serialize() = mapOf(
        "uuid" to uuid.toString(),
        "name" to name,
        "worlds" to worlds,
        "executables" to executables,
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
            executables = map["executables"] as MutableList<Executable>,
        )
        
    }
    
}