package dev.hawu.plugins.queuedsudo

import dev.hawu.plugins.queuedsudo.QueuedSudo._instance
import org.bukkit.plugin.java.JavaPlugin

/**
 * The main entrypoint of this plugin.
 */
class QueuedSudo extends JavaPlugin :

   override def onEnable(): Unit =
      _instance = Some(this)
      LocalI18n.onEnable(this)
      DataManager.init(this)
      BaseCommand(this)

   override def onDisable(): Unit =
      DataManager.onDisable()
      LocalI18n.onDisable()

/**
 * Companion object for [[QueuedSudo]].
 */
object QueuedSudo:

   private var _instance: Option[QueuedSudo] = None
   private var _version: Option[String] = None

   /**
    * Retrieves the singleton instance statically.
    *
    * @return the singleton instance
    */
   def instance: QueuedSudo = _instance.get

   /**
    * Retrieves the currently plugin version.
    * @return the version
    */
   def version: String =
      if _version.isEmpty then
         _version = Some(instance.getDescription.getVersion)
      _version.get   
