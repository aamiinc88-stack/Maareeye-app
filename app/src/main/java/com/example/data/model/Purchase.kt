package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchases")
data class Purchase(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productName: String,
    val category: String,
    val quantity: Int,
    val price: Double,
    val date: Long = System.currentTimeMillis()
)
