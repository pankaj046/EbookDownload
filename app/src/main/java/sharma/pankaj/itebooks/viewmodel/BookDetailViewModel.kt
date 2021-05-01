package sharma.pankaj.itebooks.viewmodel

import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.annotations.SerializedName
import com.squareup.picasso.Picasso
import sharma.pankaj.itebooks.R
import sharma.pankaj.itebooks.data.repository.HomeRepository
import sharma.pankaj.itebooks.listener.HomeRequestListener
import sharma.pankaj.itebooks.util.Coroutines

class BookDetailViewModel(private val repository: HomeRepository) : ViewModel() {

    companion object {
        @JvmStatic
        @BindingAdapter("image")
        fun addImage(imageView: ImageView, imageUrl: String?) {
            Picasso.get().load(imageUrl)
                .error(R.drawable.ic_placeholder_image_24)
                .tag("loading...")
                .into(imageView)
        }
    }

    var listener: HomeRequestListener? = null
    private val TAG = "BookDetailViewModel"

    var title: String? = null
    var imageUrl: String? = null
    var bookName: String? = null
    var author: String? = null
    var isbnNumber: String? = null
    var year: Int? = null
    var pages: Int? = null
    var language: String? = null
    var fileSize: String? = null
    var fileFormat: String? = null
    var description: String? = null
    var pdfUrl: String? = null

    constructor(
        repository: HomeRepository,
        title: String?,
        imageUrl: String?,
        bookName: String?,
        author: String?,
        isbnNumber: String?,
        year: Int?,
        pages: Int?,
        language: String?,
        fileSize: String?,
        fileFormat: String?,
        description: String?,
        pdfUrl: String?
    ) : this(repository) {
        this.title = title
        this.imageUrl = imageUrl
        this.bookName = bookName
        this.author = author
        this.isbnNumber = isbnNumber
        this.year = year
        this.pages = pages
        this.language = language
        this.fileSize = fileSize
        this.fileFormat = fileFormat
        this.description = description
        this.pdfUrl = pdfUrl
    }



    fun sendBookDetailsRequest(id: String) {
        listener?.onStartRequest()
        try {
            Coroutines.main {
                val response = repository.getBookDetails("detail/$id")
                response.data.let {
                    BookDetailViewModel(
                        repository,
                        it.title,
                        it.imageUrl,
                        it.bookName,
                        it.author,
                        it.isbnNumber,
                        it.year,
                        it.pages,
                        it.language,
                        it.fileSize,
                        it.fileFormat,
                        it.description,
                        it.pdfUrl
                    )
                    listener?.onHomeResponse(it)
                }
            }
        } catch (e: Exception) {
            listener?.onMessage("" + e.localizedMessage)
            Log.e(TAG, "sendBookDetailsRequest: ${e.localizedMessage}")
            listener?.onStopRequest()
        }
    }

}