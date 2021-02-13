package sharma.pankaj.itebooks.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import androidx.recyclerview.widget.LinearLayoutManager
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import sharma.pankaj.itebooks.R
import sharma.pankaj.itebooks.data.db.entities.Data
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        val binding: ActivityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        val viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        viewModel.listener = this
        setSupportActionBar(binding.toolbar)

        dialog = progressDialog(this)
        viewModel.onRequest()
        viewModel.sendMenuRequest()

        viewModel.menuList.observeForever {
            if (it.isNotEmpty()){
               for (list in it){
                   Log.e(TAG, "onCreate: ${list.title}")

                   if (list.tag!=null && list.tag.size>0){
                       for (submenu in list.tag){
                           Log.e(TAG, "onCreate: ${submenu.subTitle}")
                       }
                   }

                }
            }
        }

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
                Log.e(TAG, "onCreate: NULL $it")
            }
        }

        binding.refresh.setOnRefreshListener {
            viewModel.onRequest()
        }
    }

    override fun onMessage(msg: String) {
        toast(msg)
    }

    override fun onStartRequest() {

        dialog?.show()
    }

    override fun onStopRequest() {
        dialog?.dismiss()
    }


    override fun onHomeResponse(data: List<Data>) {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        //menuInflater.inflate(R.menu.home, menu)
        return true
    }

}