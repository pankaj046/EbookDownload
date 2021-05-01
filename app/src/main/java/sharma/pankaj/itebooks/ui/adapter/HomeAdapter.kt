package sharma.pankaj.itebooks.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import sharma.pankaj.itebooks.R
import sharma.pankaj.itebooks.databinding.BookBinding
import sharma.pankaj.itebooks.listener.ItemClickListener
import sharma.pankaj.itebooks.viewmodel.HomeViewModel

class HomeAdapter(private val context: Context, private val list: List<HomeViewModel>, private val listener : ItemClickListener) :
    RecyclerView.Adapter<HomeAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val bookBinding: BookBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.book_item_layout, parent, false)
        return CustomViewHolder(bookBinding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val homeViewModel = list[position]
        holder.bind(homeViewModel, listener)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class CustomViewHolder(private val bookBinding: BookBinding) :
        RecyclerView.ViewHolder(bookBinding.root) {

        fun bind(homeViewModel: HomeViewModel, listener: ItemClickListener) {
            this.bookBinding.datamodel = homeViewModel
            bookBinding.rootLayout.setOnClickListener {
                listener.onItemClick(homeViewModel.id!!)
            }
        }
    }
}