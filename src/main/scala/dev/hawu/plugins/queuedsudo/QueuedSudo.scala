package dev.hawu.plugins.queuedsudo

import org.bukkit.plugin.java.JavaPlugin

/**
 * The main entrypoint of this plugin.
 */
class QueuedSudo extends JavaPlugin :

   override def onEnable(): Unit =
      LocalI18n.onEnable(this)

   override def onDisable(): Unit =
      LocalI18n.onDisable()
