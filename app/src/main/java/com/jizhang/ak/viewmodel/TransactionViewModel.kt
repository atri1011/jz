package com.jizhang.ak.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jizhang.ak.data.TransactionDao
import com.jizhang.ak.data.TransactionItem
import com.jizhang.ak.data.TransactionType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest // Added
import kotlinx.coroutines.flow.MutableStateFlow // Added
import kotlinx.coroutines.flow.update // Added
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar // Added
import java.util.Locale
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.ExperimentalCoroutinesApi // 确保导入

// Data class for daily net income
data class DailyNetIncome(val date: String, val netAmount: Double)

// Data class for daily financial summary
data class DailyFinancialSummary(
    val date: String, // "MM-dd" for display
    val totalIncome: Double,
    val totalExpenses: Double
)

@OptIn(ExperimentalCoroutinesApi::class) // 添加注解以处理 flatMapLatest 等实验性 API
class TransactionViewModel(private val transactionDao: TransactionDao) : ViewModel() {

    private val _currentCalendar = MutableStateFlow(Calendar.getInstance())
    val selectedYearMonth: StateFlow<String> = _currentCalendar.map { cal ->
        String.format("%d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), getCurrentYearMonthString())

    // Helper to get initial YYYY-MM string
    private fun getCurrentYearMonthString(): String {
        val cal = Calendar.getInstance()
        return String.format("%d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
    }

    fun selectPreviousMonth() {
        _currentCalendar.update { currentCal ->
            (currentCal.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
        }
    }

    fun selectNextMonth() {
        _currentCalendar.update { currentCal ->
            (currentCal.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
        }
    }

    // Replace the old transactions StateFlow
    val transactions: StateFlow<List<TransactionItem>> = selectedYearMonth.flatMapLatest { yearMonth ->
        transactionDao.getTransactionsByYearMonth(yearMonth)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flow for daily net income
    val dailyNetIncomeFlow: StateFlow<List<DailyNetIncome>> = transactions.map { trans ->
        // Assuming date in TransactionItem is "YYYY-MM-DD"
        val groupedByDate = trans.groupBy { it.date }
        val dailyNetIncomes = mutableListOf<DailyNetIncome>()

        for ((date, transactionList) in groupedByDate) {
            var dailyIncome = 0.0
            var dailyExpenses = 0.0
            transactionList.forEach {
                if (it.type == TransactionType.INCOME) {
                    dailyIncome += it.amount
                } else {
                    dailyExpenses += it.amount
                }
            }
            dailyNetIncomes.add(DailyNetIncome(date, dailyIncome - dailyExpenses))
        }
        // Sort by date
        dailyNetIncomes.sortedBy {
            try {
                LocalDate.parse(it.date, DateTimeFormatter.ISO_LOCAL_DATE)
            } catch (e: Exception) {
                // Handle cases where date might not be in ISO_LOCAL_DATE format or is invalid
                // For simplicity, put unparseable dates at the end or handle as per requirements
                LocalDate.MAX // Or some other default for sorting
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val totalIncome: StateFlow<Double> = transactions.map { list ->
        list.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val totalExpenses: StateFlow<Double> = transactions.map { list ->
        list.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val balance: StateFlow<Double> = combine(totalIncome, totalExpenses) { income, expenses ->
        income - expenses
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val expensesByCategory: StateFlow<Map<String, Double>> = transactions.map { list ->
        list.filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.categoryName }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    val dailySummariesFlow: StateFlow<List<DailyFinancialSummary>> = transactions.map { trans ->
        val today = LocalDate.now()
        val sevenDaysAgo = today.minusDays(6) // Include today, so 6 days back + today = 7 days

        val recentTransactions = trans.filter {
            try {
                val transactionDate = LocalDate.parse(it.date, DateTimeFormatter.ISO_LOCAL_DATE)
                !transactionDate.isBefore(sevenDaysAgo) && !transactionDate.isAfter(today)
            } catch (e: Exception) {
                false // Ignore transactions with unparseable dates or out of range
            }
        }

        val groupedByDate = recentTransactions.groupBy { it.date }
        val summaries = mutableListOf<DailyFinancialSummary>()
        val outputDateFormatter = DateTimeFormatter.ofPattern("MM-dd", Locale.getDefault())

        // Ensure all dates in the last 7 days are present, even if no transactions
        for (i in 0..6) {
            val date = sevenDaysAgo.plusDays(i.toLong())
            val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val displayDate = date.format(outputDateFormatter)

            val dailyTransactions = groupedByDate[dateString] ?: emptyList()

            var dailyIncome = 0.0
            var dailyExpenses = 0.0
            dailyTransactions.forEach {
                if (it.type == TransactionType.INCOME) {
                    dailyIncome += it.amount
                } else {
                    dailyExpenses += it.amount
                }
            }
            summaries.add(DailyFinancialSummary(displayDate, dailyIncome, dailyExpenses))
        }
        // The loop already generates them in ascending order by date
        summaries
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addTransaction(transaction: TransactionItem) {
        viewModelScope.launch {
            transactionDao.insertTransaction(transaction)
        }
    }
}

class TransactionViewModelFactory(private val dao: TransactionDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}