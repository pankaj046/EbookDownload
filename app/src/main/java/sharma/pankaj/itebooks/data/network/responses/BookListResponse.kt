package sharma.pankaj.itebooks.data.network.responses

import com.google.gson.annotations.SerializedName
import sharma.pankaj.itebooks.data.db.entities.Data

data class BookListResponse (
    @SerializedName("error") val error : Boolean,
    @SerializedName("message") val message : String,
    @SerializedName("list") val list : List<Data>

)