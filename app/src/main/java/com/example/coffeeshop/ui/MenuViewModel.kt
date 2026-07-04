package com.example.coffeeshop.ui
import android.app.Application; import androidx.lifecycle.AndroidViewModel; import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.MenuDatabase; import com.example.coffeeshop.data.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow; import kotlinx.coroutines.flow.flatMapLatest; import kotlinx.coroutines.launch

class MenuViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MenuRepository(MenuDatabase.getDatabase(application).dao())
    private val searchQuery = MutableStateFlow("")
    val items = searchQuery.flatMapLatest { query -> repository.search(query) }

    init { viewModelScope.launch { repository.initIfEmpty() } }

    fun filter(query: String) { searchQuery.value = query }
}
