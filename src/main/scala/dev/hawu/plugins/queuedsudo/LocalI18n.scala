package dev.hawu.plugins.queuedsudo

import dev.hawu.plugins.api.I18n
import dev.hawu.plugins.api.collections.tuples.Pair
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

/**
 * This project's internalization module.
 */
object LocalI18n:

   private var i18n: Option[I18n] = None
   private var plugin: Option[JavaPlugin] = None

   /**
    * Enables this module which also enables
    * the translation of keys.
    *
    * @param pl The main plugin.
    */
   def onEnable(pl: JavaPlugin): Unit =
      plugin = Some(pl)
      i18n = Some(I18n(pl))

   /**
    * Disables this module and clears up any references.
    */
   def onDisable(): Unit =
      i18n = None
      plugin = None

   extension (key: String) {

      /**
       * Translate a key with arguments to fill.
       *
       * @param args the arguments to fill with.
       * @return the translated string.
       */
      def tl(args: (String, Any)*): String = i18n.get.tl(key, args.map((a, b) => Pair(a, b)): _*)

   }

   extension (sender: CommandSender) {

      /**
       * Translates a key and immediately sends it to the sender.
       *
       * @param key  the key to translate.
       * @param args the arguments to fill with.
       */
      def tl(key: String, args: (String, Any)*): Unit = sender.sendMessage(key.tl(args: _*))

   }
