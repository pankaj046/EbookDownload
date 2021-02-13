package sharma.pankaj.itebooks.data.network

import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import sharma.pankaj.itebooks.util.ApiException
import java.lang.StringBuilder

abstract class SafeApiRequest {

    suspend fun <T : Any> anyRequest(call: suspend () -> Response<T>): T {
        val response = call.invoke()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            val error = response.errorBody()?.string()
            val message = StringBuilder()
            error?.let {
                try {
//                    val message =
                    message.append(JSONObject(it).getString("message"))
                } catch (e: JSONException) {
                }
                message.append("\n")

            }
            message.append("Error Code: ${response.code()}")
            throw ApiException(message.toString())
        }
    }
}