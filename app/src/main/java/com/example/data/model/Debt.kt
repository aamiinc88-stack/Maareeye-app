package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DebtType {
    OWED_TO_USER, // Money someone owes to the user
    OWED_BY_USER  // Money the user owes to someone else
}

@Entity(tableName = "debts")
data class Debt(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val personName: String,
    val amount: Double,
    val type: DebtType,
    val date: Long = System.currentTimeMillis(),
    val notes: String,
    val isPaid: Boolean = false,
    val dueDate: Long? = null
)
