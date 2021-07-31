package dev.hawu.plugins.queuedsudo.executables

import dev.hawu.plugins.api.utils.Strings.toUUID
import dev.hawu.plugins.api.utils.Tasks
import dev.hawu.plugins.queuedsudo.QueuedSudo
import org.apache.commons.lang.builder.HashCodeBuilder
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import sun.audio.AudioPlayer.player
import java.util.*

class AwaitExecutable(
    private val value: String,
    private val flag: ExecutableFlag,
    private val duration: Long,
): Executable {
    
    override fun serialize() = mapOf(
        "value" to value,
        "flag" to flag.ordinal,
        "duration" to duration,
    )
    
    override fun run(player: UUID) {
        Tasks.runLater(plugin, duration) {
            val p = Bukkit.getPlayer(player) ?: return@runLater
            Executable.executeCommand(p, value, flag)
        }
    }
    
    override fun hashCode() = HashCodeBuilder().append(value).append(flag.ordinal).append(duration).toHashCode()
    override fun equals(other: Any?) = other is AwaitExecutable && other.value == value && other.flag == flag && other.duration == duration
    override fun toString() = "AwaitExecutable{value=$value,flag=${flag.ordinal},duration=$duration}"
    
    companion object {
        
        private val plugin = JavaPlugin.getPlugin(QueuedSudo::class.java)
        
        @JvmStatic
        fun deserialize(map: Map<String, Any?>) = AwaitExecutable(
            value = map["value"]!!.toString(),
            flag = ExecutableFlag.fromId(map["flag"].toString().toInt()),
            duration = map["duration"].toString().toLong(),
        )
        
    }
    
}