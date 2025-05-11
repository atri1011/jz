package com.jizhang.ak.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.ArrowLeft // Added
import androidx.compose.material.icons.automirrored.filled.ArrowRight // Added
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jizhang.ak.data.TransactionItem // 导入新的 TransactionItem
import com.jizhang.ak.data.TransactionType // 导入新的 TransactionType
import com.jizhang.ak.ui.theme.JzTheme
import com.jizhang.ak.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar // Added
import java.util.Date // Added
import java.util.Locale // Added
import java.util.TimeZone // Added, though not strictly necessary for this exact format but good for completeness


// 单个交易项卡片 - 更新以使用新的 TransactionItem 和处理 iconResId
@Composable
fun TransactionRow(item: TransactionItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (item.type == TransactionType.EXPENSE) MaterialTheme.colorScheme.errorContainer
                        else MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                // 使用占位符图标或根据类型选择图标
                val iconToShow = when (item.type) {
                    TransactionType.INCOME -> Icons.AutoMirrored.Filled.TrendingUp
                    TransactionType.EXPENSE -> Icons.AutoMirrored.Filled.TrendingDown
                }
                // 如果 item.iconResId 不为 null, 理想情况下应该加载该资源ID对应的图标
                // 这里我们暂时使用一个基于类型的默认图标
                Icon(
                    imageVector = item.iconResId?.let { /* Load actual icon if available */ Icons.Filled.ShoppingCart } ?: iconToShow,
                    contentDescription = item.categoryName,
                    modifier = Modifier.size(24.dp),
                    tint = if (item.type == TransactionType.EXPENSE) MaterialTheme.colorScheme.onErrorContainer
                           else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.categoryName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Text(
                        text = (if (item.type == TransactionType.EXPENSE) "- ¥" else "+ ¥") + "%.2f".format(item.amount),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = if (item.type == TransactionType.EXPENSE) Color(0xFFE53935) else Color(0xFF43A047)
                    )
                }
                if (item.note != null) {
                    Text(item.note, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Text(
            text = item.date, // item.date 现在是 String
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 16.dp, bottom = 8.dp)
        )
    }
}

// 交易列表屏幕
@Composable
fun TransactionListScreen(transactionViewModel: TransactionViewModel = viewModel()) {
    val transactions by transactionViewModel.transactions.collectAsState()
    val selectedYearMonthText by transactionViewModel.selectedYearMonth.collectAsState() // "YYYY-MM"
    var searchQuery by remember { mutableStateOf("") }

    // Format selectedYearMonthText for display, e.g., "YYYY年MM月"
    val displayMonthFormat = remember { SimpleDateFormat("yyyy年MM月", Locale.getDefault()) }
    val internalMonthFormat = remember { SimpleDateFormat("yyyy-MM", Locale.getDefault()) }

    val displayableMonth = remember(selectedYearMonthText) {
        try {
            val date = internalMonthFormat.parse(selectedYearMonthText)
            if (date != null) {
                displayMonthFormat.format(date)
            } else {
                selectedYearMonthText // Fallback
            }
        } catch (e: Exception) {
            selectedYearMonthText // Fallback in case of parsing error
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp) // Add horizontal padding for the whole column
        ) {
            // Mock Status Bar (if needed, or remove if system handles it)
            // MockStatusBar() // Consider if this is still needed or how it fits the new design

            Spacer(modifier = Modifier.height(16.dp)) // Space from top

            // Title
            Text(
                text = "交易流水",
                style = MaterialTheme.typography.headlineMedium, // Or a larger style
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("搜索交易记录") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "搜索") },
                singleLine = true
            )

            // Month Selector UI
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp), // Add padding below month selector
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { transactionViewModel.selectPreviousMonth() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowLeft, contentDescription = "上个月")
                }
                Text(
                    text = displayableMonth,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = { transactionViewModel.selectNextMonth() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowRight, contentDescription = "下个月")
                }
            }

            // Transactions List
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("暂无交易记录", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        // .background(MaterialTheme.colorScheme.background) // Background is now on the parent Column/Scaffold
                        .padding(horizontal = 0.dp) // Adjust if list items have their own padding
                ) {
                    items(transactions.filter {
                        // Basic search filter (case-insensitive)
                        // You might want to search in categoryName, note, etc.
                        it.categoryName.contains(searchQuery, ignoreCase = true) ||
                        (it.note?.contains(searchQuery, ignoreCase = true) == true)
                    }.sortedByDescending { it.date }) { transaction ->
                        TransactionRow(item = transaction)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewTransactionListScreen() {
    JzTheme {
        // For preview, we can pass a ViewModel with some sample data
        val previewViewModel: TransactionViewModel = viewModel()
        // Add some sample data for preview if needed, or rely on default empty state
        previewViewModel.addTransaction(TransactionItem(amount = 100.0, type = TransactionType.EXPENSE, categoryName = "餐饮", date = "2024-05-10", note = "午餐"))
        previewViewModel.addTransaction(TransactionItem(amount = 2000.0, type = TransactionType.INCOME, categoryName = "工资", date = "2024-05-09"))
        TransactionListScreen(transactionViewModel = previewViewModel)
    }
}