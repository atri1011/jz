package com.jizhang.ak.ui

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType // 导入 MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jizhang.ak.data.TransactionItem
import com.jizhang.ak.data.TransactionType
import com.jizhang.ak.ui.theme.JzTheme
import com.jizhang.ak.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(navController: NavController, transactionViewModel: TransactionViewModel = viewModel()) {
    var transactionType by remember { mutableStateOf(TransactionType.EXPENSE) }
    val tabs = listOf("支出", "收入")
    val selectedTabIndex = if (transactionType == TransactionType.EXPENSE) 0 else 1

    var amountString by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var category by remember { mutableStateOf("") }
    val categories = listOf("餐饮", "交通", "购物", "娱乐", "工资", "投资") // 示例分类
    var note by remember { mutableStateOf("") }
    var expandedCategoryDropdown by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val newDate = Calendar.getInstance()
            newDate.set(year, month, dayOfMonth)
            selectedDate = newDate
        },
        selectedDate.get(Calendar.YEAR),
        selectedDate.get(Calendar.MONTH),
        selectedDate.get(Calendar.DAY_OF_MONTH)
    )

    fun resetInputFields() {
        amountString = ""
        selectedDate = Calendar.getInstance()
        category = ""
        note = ""
        transactionType = TransactionType.EXPENSE
    }

    Scaffold(
        topBar = {
            Column {
                MockStatusBar()
                ScreenTitle("记一笔")
            }
        },
        bottomBar = {
            Button(
                onClick = {
                    val amountDouble = amountString.toDoubleOrNull()
                    if (amountDouble == null || amountDouble <= 0) {
                        Toast.makeText(context, "请输入有效的金额", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (category.isBlank()) {
                        Toast.makeText(context, "请选择或输入分类", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val newTransaction = TransactionItem(
                        amount = amountDouble,
                        type = transactionType,
                        categoryName = category,
                        date = dateFormatter.format(selectedDate.time),
                        note = note.takeIf { it.isNotBlank() }
                    )
                    transactionViewModel.addTransaction(newTransaction)
                    Toast.makeText(context, "记账成功", Toast.LENGTH_SHORT).show()
                    resetInputFields()
                    navController.popBackStack() // 返回上一屏幕，即交易流水
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp)
            ) {
                Text("保存")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            transactionType = if (index == 0) TransactionType.EXPENSE else TransactionType.INCOME
                        },
                        text = { Text(title) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = amountString,
                onValueChange = { amountString = it.filter { char -> char.isDigit() || char == '.' } },
                label = { Text("金额") },
                leadingIcon = { Text("¥", style = MaterialTheme.typography.bodyLarge) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = dateFormatter.format(selectedDate.time),
                onValueChange = { /* 只读, 通过按钮修改 */ },
                label = { Text("日期") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Filled.CalendarToday, contentDescription = "选择日期")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expandedCategoryDropdown,
                onExpandedChange = { expandedCategoryDropdown = !expandedCategoryDropdown },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it /* 允许手动输入 */ },
                    readOnly = false, // 允许手动输入或从下拉选择
                    label = { Text("分类") },
                    leadingIcon = { Icon(Icons.Filled.Category, contentDescription = "分类图标") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoryDropdown) },
                    modifier = Modifier
                        .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedCategoryDropdown,
                    onDismissRequest = { expandedCategoryDropdown = false }
                ) {
                    categories.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                category = selectionOption
                                expandedCategoryDropdown = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("备注 (可选)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp),
                maxLines = 5
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddTransactionScreen() {
    JzTheme {
        // Preview 需要一个 NavController 实例
        AddTransactionScreen(navController = rememberNavController())
    }
}