package dev.hawu.plugins.queuedsudo.executables

import dev.hawu.plugins.api.utils.Strings.toUUID
import dev.hawu.plugins.api.utils.Tasks
import dev.hawu.plugins.queuedsudo.QueuedSudo
import org.apache.commons.lang.builder.HashCodeBuilder
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import sun.audio.AudioPlayer.player
import java.util.*

class RepeatingExecutable(
    private val value: String,
    private val flag: ExecutableFlag,
    private val times: Int,
    private val interval: Long,
) : Executable {
    
    override fun serialize() = mapOf(
        "value" to value,
        "flag" to flag.id,
        "times" to times,
        "interval" to interval,
    )
    
    override fun run(player: UUID) {
        var executed = 0
        Tasks.runTimer(plugin, 0, interval) {
            if(executed < times) {
                executed++
                val p = Bukkit.getPlayer(player) ?: return@runTimer
                Executable.executeCommand(p, value, flag)
            }
        }
    }
    
    override fun hashCode() = HashCodeBuilder().append(value).append(flag.ordinal).append(times).append(interval).toHashCode()
    override fun equals(other: Any?) = other is RepeatingExecutable && other.value == value && other.flag == flag && other.times == times && other.interval == interval
    override fun toString() = "RepeatingExecutable{value=$value,flag=${flag.ordinal},times=$times,interval=$interval}"
    
    companion object {
        
        private val plugin = JavaPlugin.getPlugin(QueuedSudo::class.java)
        
        @JvmStatic
        fun deserialize(map: Map<String, Any>) = RepeatingExecutable(
            value = map["value"]!!.toString(),
            flag = ExecutableFlag.fromId(map["flag"].toString().toInt()),
            times = map["items"].toString().toInt(),
            interval = map["interval"].toString().toLong(),
        )
        
    }
    
}