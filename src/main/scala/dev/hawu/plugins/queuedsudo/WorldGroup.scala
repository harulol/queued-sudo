package dev.hawu.plugins.queuedsudo

import org.bukkit.configuration.serialization.ConfigurationSerializable
import dev.hawu.plugins.queuedsudo.executables.Executable

import java.util
import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

/**
 * Represents a group of worlds.
 *
 * @param name   the name of the group
 * @param worlds the worlds in the group
 */
case class WorldGroup(
   var name: String,
   worlds: mutable.Set[String] = mutable.Set[String](),
) extends ConfigurationSerializable :

   /**
    * Serializes the world group into a data map.
    *
    * @return the data map
    */
   override def serialize(): util.Map[String, Any] = mutable.Map(
      "name" -> name,
      "worlds" -> worlds.toList.asJava
   ).asJava

   override def hashCode(): Int = name.hashCode

   override def equals(obj: Any): Boolean = obj match
      case that: WorldGroup => that.name == this.name
      case _ => false

   override def toString: String = s"WorldGroup(name=$name,worlds=$worlds)"

/**
 * Companion object for [[WorldGroup]].
 */
object WorldGroup:

   /**
    * Deserializes the data map into a world group.
    *
    * @param map the data map
    * @return the world group
    */
   def deserialize(map: util.Map[String, Any]): WorldGroup =
      val name = map.get("name").asInstanceOf[String]
      val worlds = new util.HashSet(map.get("worlds").asInstanceOf[util.List[String]]).asScala
      WorldGroup(name, worlds)
