package dev.hawu.plugins.queuedsudo.executables

import org.apache.commons.lang.builder.HashCodeBuilder

import java.util
import java.util.UUID
import scala.jdk.CollectionConverters.*

/**
 * Represents an executable that waits a bit before being executed.
 *
 * @param uuid  the uuid of the executable
 * @param value the value of the executable
 * @param flag  the flag of the executable
 * @param chat  whether to make the player chat or perform command
 * @param delay the delay in milliseconds
 */
class AwaitExecutable(
   uuid: UUID,
   value: String,
   flag: ExecutableType,
   chat: Boolean,
   val delay: Long,
) extends Executable(uuid, value, flag, chat) :

   /**
    * Serializes the executable to a map.
    *
    * @return the map
    */
   override def serialize(): util.Map[String, Any] = Map(
      "uuid" -> uuid.toString,
      "value" -> value,
      "flag" -> flag.ordinal,
      "chat" -> chat,
      "delay" -> delay,
   ).asJava

   override def hashCode(): Int = HashCodeBuilder().append(uuid).append(value).append(flag).append(chat).append(delay).toHashCode

   override def equals(obj: Any): Boolean = obj match
      case exe: AwaitExecutable => exe.asInstanceOf[Executable].equals(this.asInstanceOf[Executable]) && exe.delay == delay
      case _ => false

   override def toString: String = s"AwaitExecutable(uuid=$uuid, value=$value, flag=$flag, chat=$chat, delay=$delay)"

/**
 * Companion object for [[AwaitExecutable]].
 */
object AwaitExecutable:

   /**
    * Deserializes the executable from a map.
    *
    * @param map the map
    * @return the executable
    */
   def deserialize(map: util.Map[String, Any]): AwaitExecutable =
      val (uuid, value, flag, chat) = Executable.unapplyFromMap(map)
      val delay = map.get("delay").toString.toLong
      AwaitExecutable(uuid, value, flag, chat, delay)
