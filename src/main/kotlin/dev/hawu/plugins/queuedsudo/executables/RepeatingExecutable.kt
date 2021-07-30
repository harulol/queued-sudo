package dev.hawu.plugins.queuedsudo.executables

import org.apache.commons.lang.builder.HashCodeBuilder

class RepeatingExecutable(
    private val value: String,
    private val flag: ExecutableFlag,
    private val times: Int,
    private val interval: Long,
) : Executable {
    
    override fun serialize() = mapOf(
        "value" to value,
        "flag" to flag.id,
        "times" to times,
        "interval" to interval,
    )
    
    override fun hashCode() = HashCodeBuilder().append(value).append(flag.ordinal).append(times).append(interval).toHashCode()
    override fun equals(other: Any?) = other is RepeatingExecutable && other.value == value && other.flag == flag && other.times == times && other.interval == interval
    override fun toString() = "RepeatingExecutable{value=$value,flag=${flag.ordinal},times=$times,interval=$interval}"
    
    companion object {
        
        @JvmStatic
        fun deserialize(map: Map<String, Any>) = RepeatingExecutable(
            value = map["value"]!!.toString(),
            flag = ExecutableFlag.fromId(map["flag"].toString().toInt()),
            times = map["items"].toString().toInt(),
            interval = map["interval"].toString().toLong(),
        )
        
    }
    
}