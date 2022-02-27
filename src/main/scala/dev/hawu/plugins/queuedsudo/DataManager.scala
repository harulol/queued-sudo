package dev.hawu.plugins.queuedsudo

import dev.hawu.plugins.api.collections.tuples.Pair
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.{Bukkit, OfflinePlayer}

import java.io.File
import java.util.UUID
import java.util.concurrent.CompletableFuture
import scala.annotation.targetName
import scala.collection.{GenIterable, GenMap, GenSeq, GenSet, mutable}
import scala.concurrent.Future
import scala.jdk.CollectionConverters.*

/**
 * The main manager for the plugin, manipulating the data
 * of world groups and queued executables.
 */
object DataManager:

   private var plugin: Option[JavaPlugin] = None
   private val groups = mutable.Map[String, WorldGroup]()
   private val users = mutable.Map[UUID, User]()

   /**
    * Initializes the manager with the plugin.
    *
    * @param plugin the plugin
    */
   def init(plugin: JavaPlugin): Unit =
      this.plugin = Some(plugin)
      loadGroups()
      loadUsers()

   /**
    * Disables the manager, saving data to disk.
    */
   def onDisable(): Unit =
      saveGroups()
      saveUsers()
      plugin = None
      groups.clear()
      users.clear()

   /**
    * Registers a group to the list.
    *
    * @param g the group
    */
   def addGroup(g: WorldGroup): Unit = groups += (g.name.toLowerCase -> g)

   /**
    * Retrieves a group from the map.
    *
    * @param name the name of the group
    * @return the group
    */
   def getGroup(name: String): Option[WorldGroup] = groups.get(name.toLowerCase)

   /**
    * Checks whether a group exists by a name.
    *
    * @param name the name of the group
    * @return whether the group exists
    */
   def existsGroup(name: String): Boolean = groups.contains(name.toLowerCase)

   /**
    * Removes a group from the map.
    *
    * @param name the name of the group
    */
   def removeGroup(name: String): Unit = groups -= name.toLowerCase

   /**
    * Renames a group.
    *
    * @param g    the group
    * @param name the new name of the group
    * @return whether the operation was successful.
    */
   def renameGroup(g: WorldGroup, name: String): Boolean =
      if existsGroup(name) then return false
      removeGroup(name)
      g.name = name
      addGroup(g)
      true

   /**
    * Retrieves all groups.
    *
    * @return the groups
    */
   def getGroups: Iterable[WorldGroup] = groups.values

   /**
    * Registers a user.
    *
    * @param u the user
    */
   def addUser(u: User): Unit = users += (u.uuid -> u)

   /**
    * Retrieves a user by a player instance.
    *
    * @param op the player
    */
   def addUser(op: OfflinePlayer): Unit = addUser(User(op.getUniqueId))

   /**
    * Removes a user.
    *
    * @param uuid the uuid of the user
    */
   def removeUser(uuid: UUID): Unit = users -= uuid

   /**
    * Retrieves a user by a uuid.
    *
    * @param uuid the uuid
    * @return the user option
    */
   def getUser(uuid: UUID): Option[User] = users.get(uuid)

   /**
    * Retrieves a user from an offline player.
    *
    * @param op the player
    * @return the user
    */
   def getUser(op: OfflinePlayer): Option[User] = getUser(op.getUniqueId)

   /**
    * Extension method for API pairs.
    */
   extension[L, R] (pair: Pair[L, R]) {

      /**
       * Extracts a pair.
       *
       * @return the pair
       */
      def unpack: (L, R) = (pair.getFirst, pair.getSecond)

   }

   private def loadGroups(): Unit =
      val file = File(plugin.get.getDataFolder, "groups.yml")
      if !file.exists() then return file.createNewFile()
      val config = YamlConfiguration.loadConfiguration(file)
      config.getList("groups").asInstanceOf[List[WorldGroup]].foreach(addGroup)

   private def loadUsers(): Unit =
      val file = File(plugin.get.getDataFolder, "users.yml")
      if !file.exists() then
         file.createNewFile()
         Bukkit.getOfflinePlayers.foreach(addUser)
         return ()

      val config = YamlConfiguration.loadConfiguration(file)
      config.getList("users").asInstanceOf[List[User]].foreach(addUser)

   private def saveGroups(): Unit =
      val file = File(plugin.get.getDataFolder, "groups.yml")
      if !file.exists() then file.createNewFile()
      val config = YamlConfiguration.loadConfiguration(file)
      config.set("groups", groups.values.toList.asJava)
      config.save(file)

   private def saveUsers(): Unit =
      val file = File(plugin.get.getDataFolder, "users.yml")
      if !file.exists() then file.createNewFile()
      val config = YamlConfiguration.loadConfiguration(file)
      config.set("users", users.values.toList.asJava)
      config.save(file)
