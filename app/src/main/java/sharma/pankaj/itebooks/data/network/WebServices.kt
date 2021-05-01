package sharma.pankaj.itebooks.data.network

import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import sharma.pankaj.itebooks.data.network.responses.BookListResponse
import sharma.pankaj.itebooks.data.network.responses.DetailsBookResponse
import sharma.pankaj.itebooks.data.network.responses.MenuResponse
import java.util.concurrent.TimeUnit

interface WebServices {

    @GET
    suspend fun homeRequest(@Url url: String): Response<BookListResponse>

    @GET
    suspend fun menuRequest(@Url url: String): Response<MenuResponse>

    @GET
    suspend fun bookDetails(@Url url: String): Response<DetailsBookResponse>

    companion object {
        operator fun invoke(networkConnectionInterceptor: NetworkConnectionInterceptor): WebServices {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(networkConnectionInterceptor)
                .readTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .build()
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://itebookapi.herokuapp.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WebServices::class.java)
        }
    }
}