package com.example.coffeeshop.data
import android.content.Context; import androidx.room.*; import com.example.coffeeshop.model.MenuItem; import kotlinx.coroutines.flow.Flow

@Dao
interface MenuDao {
    @Query("SELECT * FROM menu_items ORDER BY name ASC") fun getAll(): Flow<List<MenuItem>>
    @Query("SELECT * FROM menu_items WHERE name LIKE '%' || :query || '%'") fun search(query: String): Flow<List<MenuItem>>
    @Query("SELECT COUNT(*) FROM menu_items") suspend fun count(): Int
    @Insert suspend fun insertAll(items: List<MenuItem>)
}

@Database(entities = [MenuItem::class], version = 1, exportSchema = false)
abstract class MenuDatabase : RoomDatabase() {
    abstract fun dao(): MenuDao
    companion object {
        @Volatile private var INSTANCE: MenuDatabase? = null
        fun getDatabase(context: Context): MenuDatabase = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(context.applicationContext, MenuDatabase::class.java, "menu_db").build().also { INSTANCE = it }
        }
    }
}

class MenuRepository(private val dao: MenuDao) {
    val allItems: Flow<List<MenuItem>> = dao.getAll()
    fun search(query: String): Flow<List<MenuItem>> = if (query.isBlank()) dao.getAll() else dao.search(query)
    suspend fun initIfEmpty() { if (dao.count() == 0) dao.insertAll(defaultMenu()) }

    companion object {
        fun defaultMenu() = listOf(
            MenuItem(name = "Espresso", price = 2.99, category = "Hot Coffee", imageUrl = ""),
            MenuItem(name = "Cappuccino", price = 3.99, category = "Hot Coffee", imageUrl = ""),
            MenuItem(name = "Latte", price = 4.49, category = "Hot Coffee", imageUrl = ""),
            MenuItem(name = "Iced Coffee", price = 3.49, category = "Cold Drinks", imageUrl = ""),
            MenuItem(name = "Cold Brew", price = 4.99, category = "Cold Drinks", imageUrl = ""),
            MenuItem(name = "Mocha", price = 4.99, category = "Hot Coffee", imageUrl = "")
        )
    }
}
