package sharma.pankaj.itebooks

import android.app.Application
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import sharma.pankaj.itebooks.data.network.NetworkConnectionInterceptor
import sharma.pankaj.itebooks.data.network.WebServices
import sharma.pankaj.itebooks.data.repository.HomeRepository
import sharma.pankaj.itebooks.ui.DetailsViewModelFactory
import sharma.pankaj.itebooks.ui.HomeViewModelFactory
import sharma.pankaj.itebooks.util.CustomLoading

class AppController : Application(), KodeinAware {

    override val kodein: Kodein
        get() = Kodein.lazy {
            import(androidXModule(this@AppController))
            bind() from singleton {
                NetworkConnectionInterceptor(instance())
            }

            bind() from singleton { WebServices(instance()) }
            bind() from singleton { HomeRepository(instance()) }
            bind() from provider { HomeViewModelFactory(instance()) }
            bind() from provider { DetailsViewModelFactory(instance()) }
        }
}