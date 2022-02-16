package dev.hawu.plugins.queuedsudo.executables

import dev.hawu.plugins.api.Tasks
import dev.hawu.plugins.queuedsudo.QueuedSudo
import org.apache.commons.lang.builder.HashCodeBuilder

import java.util
import java.util.UUID
import scala.jdk.CollectionConverters.*

/**
 * Represents an executable that will do a certain task
 * a number of times in intervals.
 *
 * @param uuid     the player to execute as
 * @param value    the value to execute
 * @param flag     the flag to execute
 * @param chat     whether to chat or perform command
 * @param delay    the delay before first execution
 * @param interval the interval between executions
 * @param times    the number of times to execute
 */
class RepeatingExecutable(
   uuid: UUID,
   value: String,
   flag: ExecutableType,
   chat: Boolean,
   val delay: Long,
   val interval: Long,
   val times: Long,
) extends Executable(uuid, value, flag, chat) :

   /**
    * Serializes this executable to a data map.
    *
    * @return the data map
    */
   override def serialize(): util.Map[String, Any] = Map(
      "uuid" -> uuid.toString,
      "value" -> value,
      "flag" -> flag.ordinal,
      "chat" -> chat,
      "delay" -> delay,
      "interval" -> interval,
      "times" -> times,
   ).asJava

   override def execute(): Unit =
      var operations = 0
      Tasks.scheduleTimer(QueuedSudo.getInstance, delay, interval, runnable => {
         if operations < times then
            super.execute()
            operations += 1
         else runnable.cancel()
      })

   override def hashCode(): Int = HashCodeBuilder().append(uuid).append(value).append(flag).append(chat).append(delay).append(interval).append(times).toHashCode

   override def toString: String = s"RepeatingExecutable(uuid=$uuid, value=$value, flag=$flag, chat=$chat, delay=$delay, interval=$interval, times=$times)"

   override def equals(obj: Any): Boolean = obj match
      case other: RepeatingExecutable =>
         other.asInstanceOf[Executable].equals(this.asInstanceOf[Executable]) && other.interval == interval && other.times == times
      case _ => false

/**
 * Companion object for [[RepeatingExecutable]].
 */
object RepeatingExecutable:

   /**
    * Deserializes the data map back into a repeating executable.
    *
    * @param map the data map
    * @return the executable
    */
   def deserialize(map: util.Map[String, Any]): RepeatingExecutable =
      val (uuid, value, flag, chat) = Executable.unapplyFromMap(map)
      val delay = map.get("delay").toString.toLong
      val interval = map.get("interval").toString.toLong
      val times = map.get("times").toString.toLong
      RepeatingExecutable(uuid, value, flag, chat, delay, interval, times)
