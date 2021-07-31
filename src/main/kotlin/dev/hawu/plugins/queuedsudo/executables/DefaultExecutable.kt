package dev.hawu.plugins.queuedsudo.executables

import dev.hawu.plugins.api.utils.Strings.toUUID
import org.apache.commons.lang.builder.HashCodeBuilder
import org.bukkit.Bukkit
import sun.audio.AudioPlayer.player
import java.util.*

class DefaultExecutable(
    private val value: String,
    private val flag: ExecutableFlag,
) : Executable {
    
    override fun serialize() = mapOf(
        "value" to value,
        "flag" to flag.id,
    )
    
    override fun run(player: UUID) {
        val p = Bukkit.getPlayer(player) ?: return
        Executable.executeCommand(p, value, flag)
    }
    
    override fun hashCode() = HashCodeBuilder().append(value).append(flag.ordinal).toHashCode()
    override fun equals(other: Any?) = other is DefaultExecutable && other.value == value && other.flag == flag
    override fun toString() = "DefaultExecutable{value=$value,flag=${flag.ordinal}}"
    
    companion object {
        
        @JvmStatic
        fun deserialize(map: Map<String, Any>) = DefaultExecutable(
            value = map["value"]!!.toString(),
            flag = ExecutableFlag.fromId(map["flag"].toString().toInt()),
        )
        
    }
    
}