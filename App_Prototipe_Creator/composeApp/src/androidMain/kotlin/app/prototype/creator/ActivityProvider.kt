package app.prototype.creator

import android.app.Activity
import java.lang.ref.WeakReference

object ActivityProvider {
    private var activityRef: WeakReference<Activity>? = null

    var current: Activity?
        get() = activityRef?.get()
        set(value) {
            activityRef = if (value != null) WeakReference(value) else null
        }
}
