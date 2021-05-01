package sharma.pankaj.itebooks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sharma.pankaj.itebooks.data.repository.HomeRepository
import sharma.pankaj.itebooks.viewmodel.BookDetailViewModel

class DetailsViewModelFactory (
    private val repository: HomeRepository
) : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BookDetailViewModel(repository) as T
    }
}