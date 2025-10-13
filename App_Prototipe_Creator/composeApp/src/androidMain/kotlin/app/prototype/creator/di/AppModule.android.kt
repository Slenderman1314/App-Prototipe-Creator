package app.prototype.creator.di

import android.content.Context
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext

actual fun initPlatformKoin() {
    // Platform-specific initialization is handled in MainActivity
}

fun initKoin(context: Context) {
    if (GlobalContext.getOrNull() == null) {
        org.koin.core.context.startKoin {
            androidContext(context)
            Napier.base(DebugAntilog())
            modules(appModule, viewModelModule)
        }
    }
}
