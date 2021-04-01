package sharma.pankaj.itebooks.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import sharma.pankaj.itebooks.R
import sharma.pankaj.itebooks.data.db.entities.Data
import sharma.pankaj.itebooks.data.network.responses.MenuList
import sharma.pankaj.itebooks.databinding.ActivityHomeBinding
import sharma.pankaj.itebooks.listener.HomeRequestListener
import sharma.pankaj.itebooks.ui.adapter.HomeAdapter
import sharma.pankaj.itebooks.util.CustomLoading.Companion.progressDialog
import sharma.pankaj.itebooks.util.toast
import sharma.pankaj.itebooks.viewmodel.HomeViewModel


class HomeActivity : AppCompatActivity(), HomeRequestListener, KodeinAware {

    override val kodein by kodein()
    private var homeAdapter: HomeAdapter? = null
    private val factory: HomeViewModelFactory by instance()
    private val TAG = "HomeActivity"
    private var dialog: Dialog? = null
    var pushRefresh: Boolean? = false
    val menuData = ArrayList<MenuList>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        val binding: ActivityHomeBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_home
        )
        val viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        viewModel.listener = this
        binding.bookList.addOnScrollListener(viewModel.scrollListener)

        dialog = progressDialog(this)
        viewModel.onRequest()
        viewModel.sendMenuRequest()

        viewModel.menuList.observeForever {
            menuData.addAll(it)
            if (it.isNotEmpty()) {
                for (data in it) {
                    addChips(data.title, binding.chipsGroup)
                    if (data.tag!=null && data.tag.size>0) {
                        for (submenu in data.tag) {
                            addChips(submenu.subTitle, binding.chipsGroup)
                        }
                    }
                }
            }
        }

        binding.chipsGroup.setOnCheckedChangeListener(ChipGroup.OnCheckedChangeListener { chipGroup, i ->
            val chip: Chip = chipGroup.findViewById(i)
            for (data in menuData) {
                if (chip.chipText.equals(data.title)){

                }
                if (data.tag != null && data.tag.size > 0) {
                    for (submenu in data.tag) {
                        if (chip.chipText.equals(submenu.subTitle)){

                        }
                    }
                }
            }

        })

        viewModel.list.observeForever {
            if (it != null) {
                homeAdapter = HomeAdapter(this@HomeActivity, it)
                binding.bookList.layoutManager = LinearLayoutManager(this)
                binding.bookList.adapter = homeAdapter
                homeAdapter!!.notifyDataSetChanged()
                if (binding.refresh.isRefreshing){
                    binding.refresh.isRefreshing = false
                }
            } else {
                Log.e(TAG, "onCreate: NULL ")
            }
        }

        binding.refresh.setOnRefreshListener {
            pushRefresh = true
            viewModel.onRequest()
        }


    }
    private fun addChips(menu: String?, chipsGroup: ChipGroup){
        val chip = Chip(this)
        chip.text = menu
        chip.setChipBackgroundColorResource(R.color.purple_500)
       chip.isCheckable = true
        chip.setTextColor(resources.getColor(R.color.white))
       // chip.setTextAppearance(R.style.ChipTextAppearance)
        chipsGroup.addView(chip);

    }

    override fun onMessage(msg: String) {
        toast(msg)
    }

    override fun onStartRequest() {
        if (pushRefresh == false)
            dialog?.show()
    }

    override fun onStopRequest() {
        if (pushRefresh == false)
          dialog?.dismiss()
    }


    override fun onHomeResponse(data: List<Data>) {

    }
}