package dev.hawu.plugins.queuedsudo

import dev.hawu.plugins.queuedsudo.QueuedSudo.instance
import org.bukkit.plugin.java.JavaPlugin

/**
 * The main entrypoint of this plugin.
 */
class QueuedSudo extends JavaPlugin :

   override def onEnable(): Unit =
      instance = Some(this)
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

   private var instance: Option[QueuedSudo] = None

   /**
    * Retrieves the singleton instance statically.
    *
    * @return the singleton instance
    */
   def getInstance: QueuedSudo = instance.get
