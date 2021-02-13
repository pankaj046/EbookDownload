package sharma.pankaj.itebooks.data.network.responses

import com.google.gson.annotations.SerializedName

data class MenuResponse(
    @SerializedName("error") val error : Boolean,
    @SerializedName("message") val message : String,
    @SerializedName("data") val data : List<MenuList>
)

data class MenuList (
    @SerializedName("title") val title : String,
    @SerializedName("id") val id : String,
    @SerializedName("tag") val tag : List<Tag>
)

data class Tag (
    @SerializedName("subTitle") val subTitle : String,
    @SerializedName("id") val id : String
)