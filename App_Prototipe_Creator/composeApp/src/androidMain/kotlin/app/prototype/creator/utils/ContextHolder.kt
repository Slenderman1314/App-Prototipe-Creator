package app.prototype.creator.utils

import android.content.Context

/**
 * Helper object to hold application context
 */
object ContextHolder {
    private lateinit var _applicationContext: Context
    
    val applicationContext: Context
        get() = _applicationContext
    
    fun initialize(context: Context) {
        _applicationContext = context.applicationContext ?: context
    }
}
