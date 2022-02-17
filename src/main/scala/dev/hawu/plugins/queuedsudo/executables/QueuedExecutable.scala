package dev.hawu.plugins.queuedsudo.executables

import org.apache.commons.lang.builder.{EqualsBuilder, HashCodeBuilder}
import org.bukkit.Bukkit

import java.util
import java.util.UUID
import scala.jdk.CollectionConverters.*

/**
 * Holds a command that should be run immediately, or ignore
 * or queued up if the player is not online.
 *
 * @param uuid  The player that should be run the command
 * @param value The command to run
 * @param flag  The flag that should be set
 */
class QueuedExecutable(
   uuid: UUID,
   group: String,
   value: String,
   flag: ExecutableType,
   chat: Boolean,
) extends Executable(uuid, group, value, flag, chat) :

   /**
    * Serializes this executable to a map of data.
    *
    * @return The map of data
    */
   override def serialize(): util.Map[String, Any] = Map(
      "uuid" -> uuid.toString,
      "group" -> group,
      "value" -> value,
      "flag" -> flag.ordinal,
      "chat" -> chat,
   ).asJava

   override def hashCode(): Int = HashCodeBuilder().append(uuid).append(group).append(value).append(flag).append(chat).hashCode()

   override def equals(obj: Any): Boolean = obj match
      case that: QueuedExecutable => that.uuid == uuid && group == that.group && that.value == value && that.flag == flag && that.chat == chat
      case _ => false
   end equals

   override def toString: String = s"QueuedExecutable(uuid=$uuid, group=$group, value=$value, flag=$flag, chat=$chat)"

/**
 * The companion object for [[QueuedExecutable]].
 */
object QueuedExecutable:

   /**
    * Deserializes the provided native map back to
    * the instant executable.
    *
    * @param map The map to deserialize
    * @return The instant executable
    */
   def deserialize(map: util.Map[String, Any]): QueuedExecutable =
      val (uuid, group, value, flag, chat) = Executable.unapplyFromMap(map)
      QueuedExecutable(uuid, group, value, flag, chat)
