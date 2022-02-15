package dev.hawu.plugins.queuedsudo.executables

import org.bukkit.Bukkit
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player

import java.util
import java.util.UUID

/**
 * Represents an executable, whether queued or not.
 */
trait Executable(
   val uuid: UUID,
   val value: String,
   val flag: ExecutableType,
   val chat: Boolean,
   val queueTime: Long = System.currentTimeMillis(),
) extends ConfigurationSerializable :

   private def operate(player: Player, value: String): Unit =
      if chat then
         player.chat(value)
      else player.performCommand(value)

   /**
    * Attempts to perform a command via the player
    * or via the console.
    */
   final def execute(): Unit =
      val player = Bukkit.getPlayer(uuid)
      flag match
         case ExecutableType.SELF => operate(player, value)
         case ExecutableType.OP =>
            val op = player.isOp
            player.setOp(true)
            operate(player, value)
            player.setOp(op)
         case ExecutableType.CONSOLE => Bukkit.dispatchCommand(Bukkit.getConsoleSender, value)
   end execute

   def hashCode(): Int

   def toString: String

   override def equals(obj: Any): Boolean = obj match
      case other: Executable => uuid == other.uuid && value == other.value && flag == other.flag && chat == other.chat && queueTime == other.queueTime
      case _ => false

object Executable:

   def unapplyFromMap(map: util.Map[String, Any]): (UUID, String, ExecutableType, Boolean) =
      val uuid = UUID.fromString(map.get("uuid").toString)
      val value = map.get("value").toString
      val flag = ExecutableType.fromOrdinal(map.get("flag").toString.toInt)
      val chat = map.get("chat").toString.toBoolean

      (uuid, value, flag, chat)
