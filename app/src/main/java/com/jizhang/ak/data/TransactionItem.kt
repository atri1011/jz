package com.jizhang.ak.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionItem(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(), // Auto-generate ID
    val amount: Double,
    val type: TransactionType, // Enum: INCOME, EXPENSE
    val categoryName: String,
    val date: String, // For simplicity, keep as String for now
    val note: String? = null,
    val iconResId: Int? = null // Placeholder for category icon
)

enum class TransactionType { INCOME, EXPENSE }