package sharma.pankaj.itebooks.viewmodel

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.squareup.picasso.Picasso
import sharma.pankaj.itebooks.data.db.entities.Data
import sharma.pankaj.itebooks.data.repository.HomeRepository
import sharma.pankaj.itebooks.listener.HomeRequestListener
import sharma.pankaj.itebooks.util.Coroutines


class HomeViewModel(private val repository: HomeRepository) : ViewModel() {


    companion object{
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(imageView: ImageView, imageUrl: String?) {
            Picasso.get().load(imageUrl).into(imageView);
        }
    }

    private val TAG = "HomeViewModel"
    var listener: HomeRequestListener? = null
    val searchKey = MutableLiveData<String>()

    var url: String? = null
    var title: String? = null
    var description: String? = null
    var list = MutableLiveData<List<HomeViewModel>>()

    constructor(repository: HomeRepository, url: String, title: String, description: String) : this(repository) {
        this.url = url
        this.title = title
        this.description = description
    }


    private val searchKeyObservable = Observer<String> {
        onEditTextChangeListener(it)
    }

    private val listObservable = Observer<List<HomeViewModel>> {

    }

    init {
        searchKey.observeForever(searchKeyObservable)
        list.observeForever(listObservable)
        sendRequest("home/0")
    }

    override fun onCleared() {
        searchKey.removeObserver(searchKeyObservable)
        list.removeObserver(listObservable)
    }

    private fun onEditTextChangeListener(key: String) {
        Log.e(TAG, "onEditTextChangeListener: key = $key")
        if (key.length > 2) {
            sendRequest("search/$key/page/0")
        }
    }

    private fun sendRequest(url: String) {
        listener?.onStartRequest()
        try {
            Coroutines.main {
                val response = repository.getData(url)
                response.list.let {
                    listener?.onHomeResponse(it)
                    listener?.onStopRequest()
                    Log.e(TAG, "sendRequest: == $list ")
                    getData(response.list)
                    return@main
                }

            }
        }catch (e: Exception){
            listener?.onStopRequest()
        }
    }

    private fun getData(data: List<Data>){
        val myList: MutableList<HomeViewModel> = mutableListOf<HomeViewModel>()
        for (i in data) {
            myList.add(HomeViewModel(repository, i.imageUrl, i.title, i.description))
        }
        list.value = myList

    }

}
