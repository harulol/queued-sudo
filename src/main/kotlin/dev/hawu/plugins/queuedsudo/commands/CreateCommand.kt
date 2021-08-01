package dev.hawu.plugins.queuedsudo.commands

import dev.hawu.plugins.api.commands.CommandSource
import dev.hawu.plugins.api.dsl.commands.CommandSpec
import dev.hawu.plugins.api.dsl.conversations.ConversationSpec.Companion.conversation
import dev.hawu.plugins.queuedsudo.I18n.tl
import dev.hawu.plugins.queuedsudo.WorldManager
import org.bukkit.Bukkit
import org.bukkit.conversations.Conversable
import org.bukkit.plugin.java.JavaPlugin

class CreateCommand(private val pl: JavaPlugin) : CommandSpec({
    
    permission.set("queued-sudo.create")
    permissionMessage.set("no-permissions".tl("perm" to "queued-sudo.create"))
    
    fun startConversation(source: CommandSource) {
        conversation {
            val group = WorldManager.createNewEmptyGroup()
            conversable.set(source.base as Conversable)
            plugin.set(pl)
            prefix.set { "create-group.prefix".tl() }
            
            booleanPrompt {
                prompt("create-group.name-request".tl())
                onInvalidInput { _ -> "invalid-boolean".tl() }
                acceptValidatedBoolean.set { _, bool -> if(!bool) this@conversation.jumpTo(2) else null }
            }
        
            stringPrompt {
                prompt("create-group.name".tl())
                accept { _, input -> group.name = input }
            }
            
            booleanPrompt {
                prompt("create-group.attach-worlds-request".tl())
                acceptValidatedBoolean.set { _, bool -> if(!bool) this@conversation.jumpTo(4) else null }
            }
            
            stringPrompt {
                prompt("create-group.attach-worlds-name".tl())
                acceptInputAction.set { ctx, input ->
                    if(input != "cancel")  {
                        when {
                            input == null || Bukkit.getWorld(input) == null -> ctx.forWhom.sendRawMessage("create-group.world-does-not-exist".tl("world" to input))
                            input in group.worlds -> ctx.forWhom.sendRawMessage("create-group.world-already-added".tl())
                            else -> {
                                group.worlds.add(input)
                                ctx.forWhom.sendRawMessage("create-group.world-added".tl("world" to input))
                            }
                        }
                        this@conversation.jumpTo(3)
                    } else null
                }
            }
            
            booleanPrompt {
                prompt("create-group.cancellation-confirmation".tl())
                acceptValidatedBoolean.set { ctx, bool ->
                    if(bool) null else {
                        if("cancel" in group.worlds) ctx.forWhom.sendRawMessage("create-group.world-does-not-exist".tl("world" to "cancel"))
                        else {
                            group.worlds.add("cancel")
                            ctx.forWhom.sendRawMessage("create-group.world-added".tl("world" to "cancel"))
                        }
                        this@conversation.jumpTo(3)
                    }
                }
            }
        
            booleanPrompt {
                prompt("create-group.confirmation".tl())
                onInvalidInput { _ -> "invalid-boolean".tl() }
                onInput { ctx, bool ->
                    if(bool) {
                        WorldManager.addGroup(group)
                        ctx.forWhom.sendRawMessage("create-group.confirmed".tl("command" to buildString {
                            append("/qs create")
                            if(group.name != null) append(" --name \"${group.name}\"")
                            group.worlds.forEach { w -> append(" --world \"$w\"") }
                        }))
                    } else {
                        ctx.forWhom.sendRawMessage("create-group.cancelled".tl())
                    }
                }
            }
        }
    }
    
    on command { source, args ->
        val (_, properties) = args.parse()
        
        if(args.isEmpty() || properties.containsKey("-?") || properties.containsKey("--help")) {
            source.tl("help-create", "version" to pl.description.version)
            return@command true
        }
        
        if(properties.containsKey("-i") || properties.containsKey("--interactive")) {
            startConversation(source)
            return@command true
        }
        
        val addedWorlds = properties.get("-w").apply { addAll(properties.get("--world")) }.distinct()
        val group = WorldManager.createNewEmptyGroup()
        group.name = properties.get("-n").firstOrNull() ?: properties.get("--name").firstOrNull()
        group.worlds.addAll(addedWorlds)
        WorldManager.addGroup(group)
        source.tl("created-group".tl(
            "name" to (group.name ?: "Unnamed"),
            "worlds" to (if(addedWorlds.isEmpty()) "&cNone" else addedWorlds.joinToString("&7, ", limit = 3, truncated = "&7and &a${addedWorlds.size - 3} &7more...") { "&a$it" })
        ))
        true
    }
    
    register
    
})