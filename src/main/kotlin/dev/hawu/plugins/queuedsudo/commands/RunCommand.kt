package dev.hawu.plugins.queuedsudo.commands

import dev.hawu.plugins.api.dsl.commands.CommandSpec
import dev.hawu.plugins.api.utils.PlayerUtils.toKnownOfflinePlayer
import dev.hawu.plugins.api.utils.PlayerUtils.toOfflinePlayer
import dev.hawu.plugins.api.utils.PlayerUtils.toPlayer
import dev.hawu.plugins.api.utils.TimeDurationConverter
import dev.hawu.plugins.queuedsudo.I18n.tl
import dev.hawu.plugins.queuedsudo.WorldManager
import dev.hawu.plugins.queuedsudo.executables.*
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.roundToLong

class RunCommand(private val pl: JavaPlugin) : CommandSpec({
    
    permission.set("queued-sudo.run")
    permissionMessage.set("no-permissions".tl("perm" to "queued-sudo.run"))
    
    on command { sender, args ->
        val (arguments, properties) = args.parse()
        
        when {
            args.isEmpty() || properties.containsKey("-?") || properties.containsKey("--help") -> {
                sender.tl("help-run", "version" to pl.description.version)
                return@command true
            }
            (properties.get("-g").isEmpty() && properties.get("--group").isEmpty()) || (properties.get("-e").isEmpty() && properties.get("--executable").isEmpty()) -> {
                sender.tl("missing-properties")
                return@command true
            }
            arguments[0].toKnownOfflinePlayer() == null || !arguments[0].toOfflinePlayer().hasPlayedBefore() -> {
                sender.tl("player-not-found")
                return@command true
            }
            (arguments[0].toPlayer()?.hasPermission("queued-sudo.exempt") == true || arguments[0].toOfflinePlayer().isOp) && sender.base !is ConsoleCommandSender -> {
                sender.tl("op-player")
                return@command true
            }
        }
        
        val groupQuery = properties.get("-g").firstOrNull() ?: properties.get("--group").first()
        val group = WorldManager.lookupGroupsByName(groupQuery, !properties.containsKey("-c"))
        
        when {
            group.isEmpty() -> {
                sender.tl("group-not-found", "group" to groupQuery)
                return@command true
            }
            group.size > 1 -> {
                sender.tl("multiple-groups")
                return@command true
            }
        }
        
        val executable = properties.get("-e").firstOrNull() ?: properties.get("--executable").first()!!
        val flag = if(properties.containsKey("--op")) ExecutableFlag.OP else if(properties.containsKey("--console")) ExecutableFlag.CONSOLE else ExecutableFlag.SELF
        
        when {
            properties.containsKey("-a") -> {
                if(properties.get("-a").firstOrNull() == null) {
                    sender.tl("missing-properties")
                    return@command true
                }
                
                val duration = TimeDurationConverter.convertTimestamp(properties.get("-a").first()).roundToLong() / 50
                group[0].queueExecutable(arguments[0].toOfflinePlayer(), AwaitExecutable(executable, flag, duration))
                sender.tl("queued-await", "player" to arguments[0])
            }
            properties.containsKey("--rv") || properties.containsKey("--rt") -> {
                if(properties.get("--rt").firstOrNull() == null || properties.get("--rv").firstOrNull() == null) {
                    sender.tl("missing-properties")
                    return@command true
                }
                if(properties.get("--rt").first().toIntOrNull() == null) {
                    sender.tl("not-a-number", "input" to properties.get("--rt").first())
                    return@command true
                }
                
                group[0].queueExecutable(arguments[0].toOfflinePlayer(), RepeatingExecutable(
                    value = executable,
                    flag = flag,
                    times = properties.get("--rt").first().toInt(),
                    interval = TimeDurationConverter.convertTimestamp(properties.get("--rv").first()).roundToLong() / 50,
                ))
                sender.tl("queued-repeating", "player" to arguments[0])
            }
            else -> {
                group[0].queueExecutable(arguments[0].toOfflinePlayer(), DefaultExecutable(executable, flag))
                sender.tl("queued-default", "player" to arguments[0])
            }
        }
        
        true
    }
    
})