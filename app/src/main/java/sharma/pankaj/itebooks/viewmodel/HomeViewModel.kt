package sharma.pankaj.itebooks.viewmodel

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import sharma.pankaj.itebooks.R
import sharma.pankaj.itebooks.data.db.entities.Data
import sharma.pankaj.itebooks.data.network.responses.MenuList
import sharma.pankaj.itebooks.data.repository.HomeRepository
import sharma.pankaj.itebooks.listener.HomeRequestListener
import sharma.pankaj.itebooks.util.Coroutines


class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    private var requestUrl: String? = null
    private var route: String? = null

    var urlChange: Boolean? = false
    var scrollCheck: Boolean? = true
    var searchKey: String? = null
    var pageNumber: Int? = 1


    var listener: HomeRequestListener? = null

    var url: String? = null
    var title: String? = null
    var description: String? = null
    var id: String? = null
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

    constructor(repository: HomeRepository, url: String, title: String, description: String, id: String) : this(
        repository
    ) {
        this.url = url
        this.title = title
        this.description = description
        this.id = id
    }

    fun onRequest() {
        route = "home/"
        requestUrl = "home/$pageNumber"
        onAction(false)
        sendRequest()
    }

    fun onRequestOnScroll() {
        Log.e(TAG, "onRequestOnScroll: $route  $pageNumber")
        requestUrl = "$route$pageNumber"
        Log.e(TAG, "onRequestOnScroll: $requestUrl")
        onAction(false)
        sendRequest()
    }

    fun onMenuClick(url: String) {
        pageNumber =  1
        requestUrl = ""
        route = url
        requestUrl = "$url$pageNumber"
        onAction(false)
        sendRequest()
    }

    fun onSearchClick(view: View) {
        if (searchKey.isNullOrEmpty()) {
            listener?.onMessage("Please Enter Search key!")
        } else {
            pageNumber = 1
            route = "search/$searchKey/page/"
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
            val totalItemCount = layoutManager.itemCount
            val lastVisible = layoutManager.findLastVisibleItemPosition()
            val endHasBeenReached = lastVisible + 8 >= totalItemCount
            if (totalItemCount > 0 && endHasBeenReached) {
                if (scrollCheck ==  true){
                    scrollCheck = false
                    pageNumber = pageNumber?.plus(1)
                    Log.e(TAG, "onScrolled===:  $pageNumber  ==  $requestUrl" )
                    onRequestOnScroll()
                }
            }
        }
    }

    private fun onAction(show: Boolean) {
        bottomSheetBehaviorState.set(if (show) BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_HIDDEN)
    }

    fun onFloatingButtonClickListener(view: View) {
        val button : FloatingActionButton =  view as FloatingActionButton
        Log.e(TAG, "onFloatingButtonClickListener: " )
        bottomSheetBehavior = if (bottomSheetBehavior){
            onAction(false)
            button.setImageResource(R.drawable.ic_menu_24)
            false
        }else{
            button.setImageResource(R.drawable.ic_close_24)
            onAction(true)
            true
        }
    }

    private fun sendRequest() {
        listener?.onStartRequest()
        try {
            Coroutines.main {
                val response = repository.getData(requestUrl.toString())
                Log.e(TAG, "onStartRequest  $requestUrl")
                response.list.let {
                    listener?.onHomeResponse(it)
                    getData(response.list)
                    listener?.onStopRequest()
                    urlChange = false
                    scrollCheck = true
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
        val myList: MutableList<HomeViewModel> = mutableListOf()
        for (i in data) {
            myList.add(HomeViewModel(repository, i.imageUrl, i.title, i.description, i.id))
        }
        list.postValue(myList.distinct())
    }
}
