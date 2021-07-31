package dev.hawu.plugins.queuedsudo.executables

import org.bukkit.Bukkit
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player
import java.util.*

interface Executable : ConfigurationSerializable {
    
    fun run(player: UUID)
    
    override fun hashCode(): Int
    override fun equals(other: Any?): Boolean
    override fun toString(): String
    
    companion object {
        
        @JvmStatic
        fun executeCommand(player: Player, value: String, flag: ExecutableFlag) {
            when(flag) {
                ExecutableFlag.SELF -> if(value.startsWith("/")) player.performCommand(value.substring(1)) else player.chat(value)
                ExecutableFlag.OP -> {
                    val isOp = player.isOp
                    player.isOp = true
                    if(value.startsWith("/")) player.performCommand(value.substring(1)) else player.chat(value)
                    player.isOp = isOp
                }
                ExecutableFlag.CONSOLE -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value)
            }
        }
        
    }
    
}