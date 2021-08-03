package dev.hawu.plugins.queuedsudo;

import dev.hawu.plugins.queuedsudo.commands.BaseCommand;
import dev.hawu.plugins.queuedsudo.executables.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class QueuedSudo extends JavaPlugin {

    private boolean apiShaded, kotlinShaded, shouldWork;

    @Override
    public void onEnable() {
        apiShaded = isApiShaded();
        kotlinShaded = isKotlinShaded();

        if(!kotlinShaded && Bukkit.getPluginManager().getPlugin("Kotlin") == null) {
            sendConsole("&c&l(!) &eQueuedSudos&c was developed in Kotlin.&r");
            sendConsole("&c&l(!) To be able to use this plugin, you can do either:&r");
            sendConsole("&c  - Install an external dependency &eKotlin&c here: &bhttps://github.com/harulol/kotlin/releases&r");
            sendConsole("&c  - Install the &aall&c version of this plugin, this includes both the library and Kotlin: &bhttps://github.com/harulol/queued-sudo/releases&r");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if(!apiShaded && Bukkit.getPluginManager().getPlugin("HikariLibrary") == null) {
            sendConsole("&c&l(!) &eQueuedSudos&c was developed against a framework.&r");
            sendConsole("&c&l(!) To be able to use this plugin, you can do either:&r");
            sendConsole("&c  - Install an external dependency &ePlugin Library&c here: &bhttps://github.com/harulol/plugin-library/releases&r");
            sendConsole("&c  - Install the &ano-lib&c version of this plugin, this includes both the library and Kotlin: &bhttps://github.com/harulol/queued-sudo/releases&r");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        shouldWork = true;
        ConfigurationSerialization.registerClass(WorldGroup.class);
        ConfigurationSerialization.registerClass(AwaitExecutable.class);
        ConfigurationSerialization.registerClass(DefaultExecutable.class);
        ConfigurationSerialization.registerClass(RepeatingExecutable.class);

        I18n.INSTANCE.init(this);
        WorldManager.INSTANCE.init(this);
        new BaseCommand(this);
        getServer().getPluginManager().registerEvents(EventsManager.INSTANCE, this);
    }

    @Override
    public void onDisable() {
        if(!shouldWork) return;

        WorldManager.INSTANCE.clear();
        Bukkit.getScheduler().cancelTasks(this);
    }

    private boolean isApiShaded() {
        try {
            Class.forName("dev.hawu.plugins.queuedsudo.libs.api.utils.Strings");
            return true;
        } catch(ClassNotFoundException classNotFoundException) {
            return false;
        }
    }

    private boolean isKotlinShaded() {
        try {
            Class.forName("dev.hawu.plugins.queuedsudo.libs.kotlin.jvm.internal.Intrinsics");
            return true;
        } catch(ClassNotFoundException classNotFoundException) {
            return false;
        }
    }

    private void sendConsole(String s) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', s));
    }

}
