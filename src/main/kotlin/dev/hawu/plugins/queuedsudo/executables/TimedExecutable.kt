package dev.hawu.plugins.queuedsudo.executables

import org.apache.commons.lang.builder.HashCodeBuilder

class TimedExecutable(
    val value: String,
    val flag: ExecutableFlag,
    val timestamp: Long,
) : Executable {
    
    override fun serialize() = mapOf(
        "value" to value,
        "flag" to flag.id,
        "timestamp" to timestamp,
    )
    
    override fun hashCode() = HashCodeBuilder().append(value).append(flag.ordinal).append(timestamp).toHashCode()
    override fun equals(other: Any?) = other is TimedExecutable && other.value == value && other.flag == flag && other.timestamp == timestamp
    override fun toString() = "TimedExecutable{value=$value,flag=${flag.ordinal},timestamp=$timestamp}"
    
    companion object {
        
        @JvmStatic
        fun deserialize(map: Map<String, Any>) = TimedExecutable(
            value = map["value"]!!.toString(),
            flag = ExecutableFlag.fromId(map["flag"].toString().toInt()),
            timestamp = map["timestamp"].toString().toLong(),
        )
        
    }
    
}