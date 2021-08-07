package dev.hawu.plugins.queuedsudo.guis

import dev.hawu.plugins.api.dsl.inventory.ItemStackSpec.Companion.itemStack
import dev.hawu.plugins.api.dsl.inventory.ItemStackSpec.Companion.named
import dev.hawu.plugins.api.dsl.inventory.ItemStackSpec.Companion.of
import dev.hawu.plugins.api.dsl.inventory.ItemStackSpec.Companion.specifically
import dev.hawu.plugins.api.dsl.inventory.PaneSpec.Companion.pane
import dev.hawu.plugins.api.inventory.ClickEvents
import dev.hawu.plugins.queuedsudo.I18n.tl
import dev.hawu.plugins.queuedsudo.WorldGroup
import dev.hawu.plugins.queuedsudo.WorldManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player

object CreateGUI {

    fun open(player: Player, group: WorldGroup) {
        pane {
            rows.set(3)
            title.set("Group Creation")
            plugin.set(GUIManager.plugin)

            position(2, 1) {
                type.set(Material.SIGN)
                meta {
                    displayName.set("&e&lGroup Name")
                    lore {
                        clear
                        +"&7Defines another alias of this group"
                        +"&7to refer to instead of using a UUID."
                        +""
                        +"&fCurrent alias:"
                        +"&a${group.name ?: "&cNone."}"
                        +""
                        +"&6Click &eto rename."
                        +"&6Right click &eto remove name."
                    }
                }
                action {
                    if (it.isLeftClick) ClickEvents.textInput("create-group.name".tl()) {
                        open(player, group.apply { name = it })
                    }.accept(it) else if (it.isRightClick) open(player, group.apply { name = null })
                }
            }

            position(6, 1) {
                type.set(Material.GRASS)
                meta {
                    displayName.set("&e&lAttached Worlds")
                    lore {
                        clear
                        +"&7Configures what worlds are available"
                        +"&7in this group."
                        +"&7A group may host many worlds and a world"
                        +"&7could be in many groups."
                        +""
                        +"&fAttached Worlds: &a${group.worlds.size}"
                        +""
                        +"&6Click &eto modify."
                    }
                }
                action {
                    modifyWorlds(player, group)
                }
            }

            position(4, 2) {
                type.set(Material.BARRIER)
                meta {
                    displayName.set("&c&lClose")
                    lore {
                        +"&7Closes this menu and cancels"
                        +"&7this group creation."
                    }
                }

                action.set(ClickEvents.CLOSE)
            }

            position(5, 2) {
                type.set(Material.COMMAND)
                meta {
                    displayName.set("&a&lCreate")
                    lore {
                        +"&7Confirms the creation of this group"
                        +"&7and adds it to the data."
                    }
                }

                action {
                    WorldManager.addGroup(group)
                    player.closeInventory()
                    player.tl("created-group", "name" to group.name, "worlds" to group.worlds.size)
                }
            }
        }.open(player)
    }

    fun modifyWorlds(player: Player, group: WorldGroup) {
        GUIManager.paginate(
            player = player,
            title = "Modify Group's worlds",
            predicate = { world, filter -> world.contains(filter, true) },
            collection = group.worlds,
            item = {
                itemStack {
                    val world: World? = Bukkit.getWorld(it)
                    type.set(
                        when (world?.environment) {
                            World.Environment.NORMAL -> Material.GRASS
                            World.Environment.NETHER -> Material.NETHERRACK
                            World.Environment.THE_END -> Material.ENDER_STONE
                            else -> Material.BEDROCK
                        }
                    )

                    meta {
                        displayName("&e&l$it")
                        lore {
                            if (world == null) +"&cUNKNOWN WORLD!"
                            +""
                            +"&6Click &eto remove."
                        }
                    }

                    action { _ ->
                        group.worlds.remove(it)
                        modifyWorlds(player, group)
                    }
                }
            },
            finishingTouches = {
                this[48] = 1 of Material.ARROW named "&aBack" specifically {
                    action {
                        open(player, group)
                    }

                    meta {
                        lore {
                            +"&7Go back to the group menu."
                        }
                    }
                }

                this[4] = 1 of Material.EYE_OF_ENDER named "&a&lAdd" specifically {
                    meta {
                        lore {
                            +"&7Adds other worlds to this group."
                            +""
                            +"&6Click &eto add."
                        }
                    }

                    action {
                        getAllWorlds(player, group)
                    }
                }
            },
        )
    }

    fun getAllWorlds(player: Player, group: WorldGroup) {
        GUIManager.paginate(
            player = player,
            predicate = { world, filter -> world.name.contains(filter, true) },
            title = "Add worlds",
            collection = Bukkit.getWorlds().filter { it.name !in group.worlds },
            item = { world ->
                itemStack {
                    type.set(
                        when (world?.environment) {
                            World.Environment.NORMAL -> Material.GRASS
                            World.Environment.NETHER -> Material.NETHERRACK
                            World.Environment.THE_END -> Material.ENDER_STONE
                            else -> Material.BEDROCK
                        }
                    )

                    meta {
                        displayName("&e&l${world.name}")
                        lore {
                            +""
                            +"&6Click &eto add."
                        }
                    }

                    action {
                        if(world.name !in group.worlds) group.worlds.add(world.name)
                        getAllWorlds(player, group)
                    }
                }
            },
            finishingTouches = {
                this[48] = 1 of Material.ARROW named "&aBack" specifically {
                    action {
                        modifyWorlds(player, group)
                    }

                    meta {
                        lore {
                            +"&7Go back to the group menu."
                        }
                    }
                }
            },
        )
    }

}