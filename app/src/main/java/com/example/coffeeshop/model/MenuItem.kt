package com.example.coffeeshop.model
import androidx.room.Entity; import androidx.room.PrimaryKey
@Entity(tableName = "menu_items")
data class MenuItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val category: String,
    val imageUrl: String
)
