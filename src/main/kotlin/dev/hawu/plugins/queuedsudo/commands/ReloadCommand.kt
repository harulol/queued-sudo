package dev.hawu.plugins.queuedsudo.commands

import dev.hawu.plugins.api.dsl.commands.CommandSpec
import dev.hawu.plugins.queuedsudo.I18n
import dev.hawu.plugins.queuedsudo.I18n.tl

class ReloadCommand : CommandSpec({
    permission.set("queued-sudo.reload")
    
    on command { source, _ ->
        source.tl("reloaded")
        I18n.reload()
        true
    }
    
    register
})