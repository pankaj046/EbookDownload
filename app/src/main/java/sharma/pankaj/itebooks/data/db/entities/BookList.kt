package sharma.pankaj.itebooks.data.db.entities

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("title") val title : String,
    @SerializedName("imageUrl") val imageUrl : String,
    @SerializedName("description") val description : String,
    @SerializedName("id") val id : String
)