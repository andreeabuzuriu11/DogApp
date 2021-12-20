package com.buzuriu.dogapp

import android.app.Application
import com.buzuriu.dogapp.services.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {

    private val appModule = module {
        single<ICurrentActivityService> { CurrentActivityService() }
        single<IDialogService> { DialogService(get()) }
        single<IFirebaseAuthService> { FirebaseAuthService() }
        single<INavigationService> { NavigationService(get()) }
        single<IConnectivityService> { ConnectivityService(get())}
        single<IDatabaseService> { DatabaseService()}
        single<IDataExchangeService> { DataExchangeService() }
        single<ISharedPreferencesService> { SharedPreferencesService(get()) }
    }

    private val activityService: ICurrentActivityService by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }

        activityService.initWithApplication(this@App)
    }
}