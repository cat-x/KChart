package pro.udax.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import xyz.a1api.tools.LStorage

/**
 * Created by Cat-x on 2018/8/8.
 * For KChart
 * Cat-x All Rights Reserved
 */
class App : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        LStorage.init(this)
    }

    companion object {
        lateinit var app: App
            private set
    }
}