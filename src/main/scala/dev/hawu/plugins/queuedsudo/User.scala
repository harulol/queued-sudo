package dev.hawu.plugins.queuedsudo

import dev.hawu.plugins.queuedsudo.executables.Executable
import org.bukkit.configuration.serialization.ConfigurationSerializable

import java.util
import java.util.UUID
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

/**
 * Represents a player for queuing executables.
 *
 * @param uuid        the uuid of the player
 * @param executables the executables to be executed
 */
case class User(
   uuid: UUID,
   executables: mutable.ArrayBuffer[Executable] = mutable.ArrayBuffer.empty[Executable]
) extends ConfigurationSerializable :

   /**
    * Serializes the user to a map.
    *
    * @return the serialized user
    */
   override def serialize(): util.Map[String, Any] = mutable.Map(
      "uuid" -> uuid.toString,
      "executables" -> executables.asJava
   ).asJava

   /**
    * Runs all queued up executables.
    */
   def run(): Unit =
      executables.foreach(_.execute())

/**
 * Companion object for [[User]].
 */
object User:

   /**
    * Deserializes the user from a map.
    *
    * @param map the map to deserialize
    * @return the deserialized user
    */
   def deserialize(map: util.Map[String, Any]): User =
      val uuid = UUID.fromString(map.get("uuid").toString)
      val executables = map.get("executables").asInstanceOf[util.List[Executable]].asScala.to(mutable.ArrayBuffer)

      User(uuid, executables)
