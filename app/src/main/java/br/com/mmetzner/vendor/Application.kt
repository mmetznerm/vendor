package br.com.mmetzner.vendor

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import br.com.mmetzner.vendor.injection.injectionModule
import org.koin.android.ext.android.startKoin

class Application : MultiDexApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
        startKoin(this, listOf(injectionModule))
    }
}