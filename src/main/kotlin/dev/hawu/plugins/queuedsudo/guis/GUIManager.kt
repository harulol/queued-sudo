package dev.hawu.plugins.queuedsudo.guis

import dev.hawu.plugins.api.dsl.inventory.ItemStackSpec.Companion.of
import dev.hawu.plugins.api.dsl.inventory.ItemStackSpec.Companion.named
import dev.hawu.plugins.api.dsl.inventory.ItemStackSpec.Companion.specifically
import dev.hawu.plugins.api.dsl.inventory.PaneSpec.Companion.pane
import dev.hawu.plugins.api.inventory.ClickEvents
import dev.hawu.plugins.api.inventory.PagedPane
import dev.hawu.plugins.api.inventory.Pane
import dev.hawu.plugins.api.inventory.UItemStack
import dev.hawu.plugins.queuedsudo.I18n.tl
import org.bukkit.Material
import org.bukkit.entity.Player

object GUIManager {

    private val previousButtons = intArrayOf(18, 27)
    private val nextButtons = intArrayOf(26, 35)

    private fun emptyPane(name: String) = pane {
        rows.set(6)
        title.set(name)
    }

    fun <T> paginate(
        player: Player,
        filter: String? = null,
        predicate: (T, String) -> Boolean,
        title: String,
        collection: Collection<T>,
        item: (T) -> UItemStack?,
        finishingTouches: Pane.() -> Unit,
    ) {
        val pagination = PagedPane()
        var pane = emptyPane(title)
        var index = 0

        fun Pane.finish() {
            this[49] = 1 of Material.BARRIER named "&c&lClose" specifically {
                meta {
                    lore {
                        +"&7Closes this menu."
                    }
                }
                action.set(ClickEvents.CLOSE)
            }

            this[50] = 1 of Material.HOPPER named "&e&lFilter" specifically {
                meta {
                    lore {
                        +"&7Filters out items from the"
                        +"&7collection."
                        +""
                        +"&fCurrent Filter"
                        +"&a${filter ?: "&cNone"}"
                        +""
                        +"&6Click &eto filter."
                        +"&6Right click &eto clear."
                    }
                }

                action {
                    if(it.isLeftClick) ClickEvents.textInput("search-filter".tl()) { s ->
                        paginate(player, s, predicate, title, collection, item, finishingTouches)
                    }.accept(it) else paginate(player, null, predicate, title, collection, item, finishingTouches)
                }
            }
        }

        collection.run { if(filter != null) this.filter { predicate(it, filter) } else this }.forEach {
            while(Pane.isBorder(index, 54)) index++
            if(index >= pane.size) {
                pane.finish()
                pagination.append(pane.apply(finishingTouches))
                pane = emptyPane(title)
                index = 0
            }

            pane[index] = item(it)
            index++
        }

        pane.finish()
        pagination.append(pane.apply(finishingTouches))
        pagination.createControls(previousButtons, nextButtons)
        pagination.open(player)
    }

}