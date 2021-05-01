package sharma.pankaj.itebooks.viewmodel

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso
import sharma.pankaj.itebooks.R
import sharma.pankaj.itebooks.data.db.entities.Data
import sharma.pankaj.itebooks.data.network.responses.MenuList
import sharma.pankaj.itebooks.data.repository.HomeRepository
import sharma.pankaj.itebooks.listener.HomeRequestListener
import sharma.pankaj.itebooks.util.Coroutines


class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    private var requestUrl: String? = null
    private var lastUrl: String? = null
    var urlChange: Boolean? = false
    var scrollCheck: Boolean? = true
    var searchKey: String? = null
    var pageNumber: Int? = 1
    val myList: MutableList<HomeViewModel> = mutableListOf()
    var scrollTo = ObservableInt()

    var listener: HomeRequestListener? = null

    var url: String? = null
    var title: String? = null
    var description: String? = null
    var list = MutableLiveData<List<HomeViewModel>>()
    var menuList = MutableLiveData<List<MenuList>>()
    private val TAG = "HomeViewModel"

    val bottomSheetBehaviorState = ObservableInt(BottomSheetBehavior.STATE_HIDDEN)
    var bottomSheetBehavior: Boolean = false;

    companion object {

        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(imageView: ImageView, imageUrl: String?) {
            Picasso.get().load(imageUrl)
                .error(R.drawable.ic_placeholder_image_24)
                .tag("loading...")
                .into(imageView);
        }

        @JvmStatic
        @BindingAdapter("app:scrollTo")
        fun scrollTo(recyclerView: RecyclerView, position: Int) {
            Log.e("TAG", "scrollTo: ")
            recyclerView.scrollToPosition(position)
        }

        @JvmStatic
        @BindingAdapter("bottomSheetState")
        fun bindingBottomSheet(container: ConstraintLayout, state: Int) {
            val behavior = BottomSheetBehavior.from(container)
            behavior.state = state
        }

    }

    constructor(repository: HomeRepository, url: String, title: String, description: String) : this(
        repository
    ) {
        this.url = url
        this.title = title
        this.description = description
    }

    fun onRequest() {
        requestUrl = "home/$pageNumber"
        onAction(false)
        sendRequest()
    }

    fun onMenuClick(url: String) {
        myList.clear()
        pageNumber =  1
        requestUrl = ""
        requestUrl = "$url$pageNumber"
        onAction(false)
        sendRequest()
    }

    fun onSearchClick(view: View) {
        if (searchKey.isNullOrEmpty()) {
            listener?.onMessage("Please Enter Search key!")
        } else {
            pageNumber = 1
            requestUrl = "search/$searchKey/page/$pageNumber"
            urlChange = true
            sendRequest()
        }
    }

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            Log.e(TAG, "onScrolled: $newState")
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisiblePosition = layoutManager.findLastVisibleItemPosition()
            if (firstVisiblePosition==myList.size-1){
                if (scrollCheck ==  true){
                    scrollCheck = false
                    pageNumber = pageNumber?.plus(1)
                    Log.e(TAG, "onScrolled:  $pageNumber")
                    onRequest()
                }
            }
        }
    }

    private fun onAction(show: Boolean) {
        bottomSheetBehaviorState.set(if (show) BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_HIDDEN)
    }

    fun onFloatingButtonClickListener(view: View) {
        Log.e(TAG, "onFloatingButtonClickListener: " )
        bottomSheetBehavior = if (bottomSheetBehavior){
            onAction(false)
            false;
        }else{
            onAction(true)
            true;
        }
    }

    private fun sendRequest() {
        listener?.onStartRequest()
        try {
            Coroutines.main {
                val response = repository.getData(requestUrl.toString())
                response.list.let {
                    listener?.onHomeResponse(it)
                    getData(response.list)
                    listener?.onStopRequest()
                    urlChange = false
                    scrollCheck = true;
                    return@main
                }
            }
        } catch (e: Exception) {
            listener?.onStopRequest()
        }
    }


    fun sendMenuRequest() {
        try {
            Coroutines.main {
                val response = repository.getMenuData("menu")
                response.data.let {
                    menuList.postValue(it)
                }
            }
        } catch (e: Exception) {
            listener?.onStopRequest()
        }
    }

    private fun getData(data: List<Data>) {
        if (myList.size > 0 && urlChange == true) {
            myList.clear()
        }
        for (i in data) {
            myList.add(HomeViewModel(repository, i.imageUrl, i.title, i.description))
        }
        list.postValue(myList.distinct())
    }
}
