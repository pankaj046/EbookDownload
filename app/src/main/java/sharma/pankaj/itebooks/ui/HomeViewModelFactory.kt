package sharma.pankaj.itebooks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sharma.pankaj.itebooks.data.repository.HomeRepository
import sharma.pankaj.itebooks.viewmodel.HomeViewModel

class HomeViewModelFactory (
    private val repository: HomeRepository
) : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}