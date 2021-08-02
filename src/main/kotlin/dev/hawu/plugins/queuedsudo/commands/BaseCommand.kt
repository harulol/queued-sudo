package dev.hawu.plugins.queuedsudo.commands

import dev.hawu.plugins.api.dsl.commands.CommandSpec
import dev.hawu.plugins.queuedsudo.I18n.tl
import org.bukkit.plugin.java.JavaPlugin

class BaseCommand(private val plugin: JavaPlugin) : CommandSpec({
    
    name.set("queuedsudo")
    permission.set("queued-sudo.main")
    permissionMessage.set("no-permissions".tl("perm" to permission.get()))
    
    "create" bindTo CreateCommand(plugin)
    "run" bindTo RunCommand(plugin)
    "reload" bindTo ReloadCommand()
    "search" bindTo SearchCommand(plugin)
    "list" bindTo ListCommand(plugin)
    "rename" bindTo RenameCommand(plugin)
    "delete" bindTo DeleteCommand(plugin)
    "addworld" bindTo AddWorldCommand(plugin)
    "removeworld" bindTo RemoveWorldCommand(plugin)
    
    on command { sender, _ ->
        sender.sendMessage("help-index".tl("version" to plugin.description.version))
        true
    }
    
    on tab { _, args -> if(args.size == 1) listOf(
        "create",
        "run",
        "reload",
        "search",
        "list",
        "rename",
        "delete",
        "addworld",
        "removeworld",
    ).filter { it.startsWith(args[0]) } else null }
    
    register to plugin
    
})