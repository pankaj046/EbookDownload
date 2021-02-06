package sharma.pankaj.itebooks.ui

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import sharma.pankaj.itebooks.R
import sharma.pankaj.itebooks.data.db.entities.Data
import sharma.pankaj.itebooks.databinding.ActivityHomeBinding
import sharma.pankaj.itebooks.listener.HomeRequestListener
import sharma.pankaj.itebooks.ui.adapter.HomeAdapter
import sharma.pankaj.itebooks.util.CustomDialog
import sharma.pankaj.itebooks.util.CustomDialog.Companion.progressDialog
import sharma.pankaj.itebooks.util.toast
import sharma.pankaj.itebooks.viewmodel.HomeViewModel

class HomeActivity : AppCompatActivity(), HomeRequestListener, KodeinAware {

    override val kodein by kodein()
    private var homeAdapter: HomeAdapter? = null
    private val factory: HomeViewModelFactory by instance()
    private val TAG = "HomeActivity"
    private val dialog: CustomDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        val binding: ActivityHomeBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_home)
        val viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        viewModel.listener = this


        viewModel.list.observeForever {

            if (it != null) {
                homeAdapter = HomeAdapter(this@HomeActivity, it)
                binding.bookList.layoutManager = LinearLayoutManager(this)
                binding.bookList.adapter = homeAdapter
                homeAdapter!!.notifyDataSetChanged()
            } else {
                Log.e(TAG, "onCreate: NULL $it")
            }
        }
    }

    override fun onMessage(msg: String) {
        toast(msg)
    }

    override fun onStartRequest() {
        progressDialog(this).show()
    }

    override fun onStopRequest() {
        progressDialog(this).dismiss()
    }

    override fun onHomeResponse(data: List<Data>) {

    }
}