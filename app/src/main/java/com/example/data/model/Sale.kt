package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val customerId: Int?,
    val quantity: Int,
    val totalAmount: Double,
    val profit: Double,
    val date: Long = System.currentTimeMillis()
)
