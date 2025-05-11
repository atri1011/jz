package com.jizhang.ak.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp // 导入 AutoMirrored 版本
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jizhang.ak.ui.theme.JzTheme
import com.jizhang.ak.data.TransactionType // 添加导入

data class CategoryItem(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val type: TransactionType // 支出或收入分类
)

@Composable
fun CategoryManagementScreen() {
    val context = LocalContext.current // 获取 Context
    val sampleCategories = listOf(
        CategoryItem("1", "餐饮美食", Icons.Filled.Fastfood, TransactionType.EXPENSE),
        CategoryItem("2", "交通出行", Icons.Filled.DirectionsBus, TransactionType.EXPENSE),
        CategoryItem("3", "服饰美容", Icons.Filled.ShoppingBag, TransactionType.EXPENSE),
        CategoryItem("4", "生活日用", Icons.Filled.ShoppingCart, TransactionType.EXPENSE),
        CategoryItem("5", "住房缴费", Icons.Filled.Home, TransactionType.EXPENSE),
        CategoryItem("6", "工资收入", Icons.Filled.MonetizationOn, TransactionType.INCOME),
        CategoryItem("7", "投资理财", Icons.AutoMirrored.Filled.TrendingUp, TransactionType.INCOME), // 使用 AutoMirrored 版本
        CategoryItem("8", "兼职外快", Icons.Filled.Work, TransactionType.INCOME),
        CategoryItem("9", "医疗健康", Icons.Filled.LocalHospital, TransactionType.EXPENSE),
        CategoryItem("10", "学习提升", Icons.Filled.School, TransactionType.EXPENSE),
    )

    Scaffold(
        topBar = {
            Column {
                MockStatusBar()
                ScreenTitle("分类管理")
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { Toast.makeText(context, "添加新分类功能待实现", Toast.LENGTH_SHORT).show() }) {
                Icon(Icons.Filled.Add, contentDescription = "添加新分类")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = 8.dp)
        ) {
            items(sampleCategories) { category ->
                CategoryRow(category = category)
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp)) // 重命名为 HorizontalDivider
            }
        }
    }
}

@Composable
fun CategoryRow(category: CategoryItem) {
    val context = LocalContext.current // 获取 Context
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (category.type == TransactionType.EXPENSE) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    category.icon,
                    contentDescription = category.name,
                    modifier = Modifier.size(24.dp),
                    tint = if (category.type == TransactionType.EXPENSE) MaterialTheme.colorScheme.onErrorContainer
                           else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(category.name, style = MaterialTheme.typography.bodyLarge)
        }
        IconButton(onClick = { Toast.makeText(context, "编辑分类 ${category.name} (模拟)", Toast.LENGTH_SHORT).show() }) {
            Icon(Icons.Filled.Edit, contentDescription = "编辑分类", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCategoryManagementScreen() {
    JzTheme {
        CategoryManagementScreen()
    }
}