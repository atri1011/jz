package com.jizhang.ak.viewmodel

import androidx.lifecycle.ViewModel
import com.jizhang.ak.data.TransactionItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TransactionViewModel : ViewModel() {
    private val _transactions = MutableStateFlow<List<TransactionItem>>(emptyList())
    val transactions: StateFlow<List<TransactionItem>> = _transactions.asStateFlow()

    fun addTransaction(transaction: TransactionItem) {
        _transactions.update { currentList ->
            currentList + transaction
        }
    }
}