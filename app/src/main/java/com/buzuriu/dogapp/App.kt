package com.buzuriu.dogapp

import android.app.Application
import com.buzuriu.dogapp.services.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class App : Application() {

    private val appModule = module {
        single<ICurrentActivityService> { CurrentActivityService() }
        single<ISnackMessageService> { SnackMessageService(get()) }
        single<IFirebaseAuthService> { FirebaseAuthService() }
        single<INavigationService> { NavigationService(get()) }
        single<IInternetService> { InternetService(get()) }
        single<IDatabaseService> { DatabaseService(get()) }
        single<IExchangeInfoService> { ExchangeInfoService() }
        single<IAlertMessageService> { AlertMessageService(get()) }
        single<IPermissionService> { PermissionService(get()) }
        single<IActivityForResultService> { ActivityForResultService() }
        single<IStorageService> { StorageService() }
        single<ILocalDatabaseService> { LocalDatabaseService() }
        single<ICsvDataService> { CsvDataService(get(), get()) }
    }

    private val activityService: ICurrentActivityService by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(appModule)
        }

        activityService.initWithApplication(this@App)
    }
}