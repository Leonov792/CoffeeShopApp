package com.example.coffeeshop.ui
import android.os.Bundle; import android.view.LayoutInflater; import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity; import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider; import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager; import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshop.databinding.ActivityMainBinding; import com.example.coffeeshop.databinding.ItemMenuBinding
import com.example.coffeeshop.model.MenuItem; import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MenuViewModel
    private lateinit var adapter: MenuAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater); setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MenuViewModel::class.java]
        adapter = MenuAdapter { item -> showPriceDialog(item) }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch { viewModel.items.collect { adapter.submitList(it) } }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { viewModel.filter(query ?: ""); return true }
            override fun onQueryTextChange(newText: String?): Boolean { viewModel.filter(newText ?: ""); return true }
        })
    }

    private fun showPriceDialog(item: MenuItem) {
        MaterialAlertDialogBuilder(this)
            .setTitle(item.name)
            .setMessage(String.format("$%.2f", item.price))
            .setPositiveButton("OK", null).show()
    }

    inner class MenuAdapter(private val onClick: (MenuItem) -> Unit) : RecyclerView.Adapter<MenuAdapter.VH>() {
        private var list = listOf<MenuItem>()
        fun submitList(newList: List<MenuItem>) { list = newList; notifyDataSetChanged() }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        override fun onBindViewHolder(holder: VH, position: Int) { holder.bind(list[position]) }
        override fun getItemCount() = list.size
        inner class VH(val b: ItemMenuBinding) : RecyclerView.ViewHolder(b.root) {
            fun bind(item: MenuItem) {
                b.name.text = item.name; b.price.text = String.format("$%.2f", item.price)
                b.category.text = item.category
                b.root.setOnClickListener { onClick(item) }
            }
        }
    }
}
