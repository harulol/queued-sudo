package dev.hawu.plugins.queuedsudo.executables

import org.bukkit.configuration.serialization.ConfigurationSerializable

interface Executable : ConfigurationSerializable {
    
    override fun hashCode(): Int
    override fun equals(other: Any?): Boolean
    override fun toString(): String
    
}