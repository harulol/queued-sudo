package dev.hawu.plugins.queuedsudo.executables

enum class ExecutableFlag(val id: Int) {
    
    SELF(0), OP(1), CONSOLE(2);
    
    companion object {
        
        @JvmStatic
        fun fromId(id: Int) = when(id) {
            0 -> SELF
            1 -> OP
            2 -> CONSOLE
            else -> throw IllegalArgumentException()
        }
        
    }
    
}