package sharma.pankaj.itebooks.data.network.responses

import com.google.gson.annotations.SerializedName

data class BookDetailResponse(
    @SerializedName("error") val error : Boolean,
    @SerializedName("message") val message : String,
    @SerializedName("data") val data : Details
)

data class Details (
    @SerializedName("title") val title : String,
    @SerializedName("imageUrl") val imageUrl : String,
    @SerializedName("bookName") val bookName : String,
    @SerializedName("author") val author : String,
    @SerializedName("isbnNumber") val isbnNumber : Int,
    @SerializedName("year") val year : Int,
    @SerializedName("pages") val pages : Int,
    @SerializedName("language") val language : String,
    @SerializedName("fileSize") val fileSize : String,
    @SerializedName("fileFormat") val fileFormat : String,
    @SerializedName("description") val description : String,
    @SerializedName("pdfUrl") val pdfUrl : String
)
