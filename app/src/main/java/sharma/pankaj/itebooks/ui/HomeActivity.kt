package sharma.pankaj.itebooks.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import sharma.pankaj.itebooks.R
import sharma.pankaj.itebooks.data.db.entities.Data
import sharma.pankaj.itebooks.databinding.ActivityHomeBinding
import sharma.pankaj.itebooks.listener.HomeRequestListener
import sharma.pankaj.itebooks.ui.adapter.HomeAdapter
import sharma.pankaj.itebooks.viewmodel.HomeViewModel

class HomeActivity : AppCompatActivity(), HomeRequestListener, KodeinAware{

    override val kodein by kodein()
    private var bookList: RecyclerView?=null
    private var homeAdapter: HomeAdapter?=null
    private val factory : HomeViewModelFactory by instance()
    private val TAG = "HomeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        val viewModel = ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)      //   .NewInstanceFactory().create(HomeViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel.listener = this

        viewModel.list.observeForever {
            Log.e(TAG, "onCreate: $it")
            if (it!=null){
                homeAdapter = HomeAdapter(this@HomeActivity, it)
                binding.bookList.layoutManager = LinearLayoutManager(this)
                binding.bookList.adapter = homeAdapter
            }else{
                Log.e(TAG, "onCreate: NULL $it" )
            }
        }


    }

    override fun onStartRequest() {
        //toast("Started")
    }

    override fun onStopRequest() {

    }

    override fun onHomeResponse(data: List<Data>) {
    }
}