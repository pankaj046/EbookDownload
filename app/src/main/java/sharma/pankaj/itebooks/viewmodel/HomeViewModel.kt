package sharma.pankaj.itebooks.viewmodel

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import sharma.pankaj.itebooks.data.db.entities.Data
import sharma.pankaj.itebooks.data.repository.HomeRepository
import sharma.pankaj.itebooks.listener.HomeRequestListener
import sharma.pankaj.itebooks.util.Coroutines


class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    var requestUrl: String? =null
    var searchKey: String? =null
    var pageNumber: Int? = 1
    val myList: MutableList<HomeViewModel> = mutableListOf()
    var scrollTo = ObservableInt()
    private val TAG = "HomeViewModel"

    companion object{

        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(imageView: ImageView, imageUrl: String?) {
            Picasso.get().load(imageUrl).into(imageView);
        }

        @JvmStatic
        @BindingAdapter("app:scrollTo")
        fun scrollTo(recyclerView: RecyclerView, position: Int) {
            Log.e("TAG", "scrollTo: ", )
            recyclerView.scrollToPosition(position)
        }
    }


    var listener: HomeRequestListener? = null
    //val searchKey = MutableLiveData<String>()

    var url: String? = null
    var title: String? = null
    var description: String? = null
    var list = MutableLiveData<List<HomeViewModel>>()

    constructor(repository: HomeRepository, url: String, title: String, description: String) : this(
        repository
    ) {
        this.url = url
        this.title = title
        this.description = description
    }


    fun onSearchClick(view: View){
        if (searchKey.isNullOrEmpty()){
            listener?.onMessage("Please Enter Search key!")
        }else{
            requestUrl = "search/$searchKey/page/$pageNumber"
            sendRequest()
        }
    }

    init {
        requestUrl = "home/$pageNumber"
        sendRequest()
    }

    private fun sendRequest() {
        listener?.onStartRequest()
        try {
            Coroutines.main {
                val response = repository.getData(requestUrl.toString())
                response.list.let {
                    listener?.onHomeResponse(it)
                    getData(response.list)
                    return@main
                }
            }
        }catch (e: Exception){
            listener?.onStopRequest()
        }
    }

    private fun getData(data: List<Data>){
        for (i in data) {
            myList.add(HomeViewModel(repository, i.imageUrl, i.title, i.description))
        }
        list.postValue(myList)
        listener?.onStopRequest()
    }
}
