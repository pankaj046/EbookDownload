package sharma.pankaj.itebooks.data.repository

import retrofit2.Call
import sharma.pankaj.itebooks.data.network.SafeApiRequest
import sharma.pankaj.itebooks.data.network.WebServices
import sharma.pankaj.itebooks.data.network.responses.BookListResponse

class HomeRepository (private val api: WebServices) : SafeApiRequest(){

    suspend fun getData(url: String) : BookListResponse {
        return anyRequest { api.homeRequest(url) }
    }

}