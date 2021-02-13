package sharma.pankaj.itebooks.data.repository

import sharma.pankaj.itebooks.data.network.SafeApiRequest
import sharma.pankaj.itebooks.data.network.WebServices
import sharma.pankaj.itebooks.data.network.responses.BookListResponse
import sharma.pankaj.itebooks.data.network.responses.MenuResponse

class HomeRepository (private val api: WebServices) : SafeApiRequest(){

    suspend fun getData(url: String) : BookListResponse {
        return anyRequest { api.homeRequest(url) }
    }

    suspend fun getMenuData(url: String) : MenuResponse {
        return anyRequest { api.menuRequest(url) }
    }

}