package dev.hawu.plugins.queuedsudo.executables

import org.apache.commons.lang.builder.HashCodeBuilder

class AwaitExecutable(
    private val value: String,
    private val flag: ExecutableFlag,
    private val duration: Long,
): Executable {
    
    override fun serialize() = mapOf(
        "value" to value,
        "flag" to flag.ordinal,
        "duration" to duration,
    )
    
    override fun hashCode() = HashCodeBuilder().append(value).append(flag.ordinal).append(duration).toHashCode()
    override fun equals(other: Any?) = other is AwaitExecutable && other.value == value && other.flag == flag && other.duration == duration
    override fun toString() = "AwaitExecutable{value=$value,flag=${flag.ordinal},duration=$duration}"
    
    companion object {
        
        @JvmStatic
        fun deserialize(map: Map<String, Any?>) = AwaitExecutable(
            value = map["value"]!!.toString(),
            flag = ExecutableFlag.fromId(map["flag"].toString().toInt()),
            duration = map["duration"].toString().toLong(),
        )
        
    }
    
}