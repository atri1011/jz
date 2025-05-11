package com.jizhang.ak.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jizhang.ak.ui.theme.JzTheme

@Composable
fun OverviewStatsScreen() {
    Scaffold(
        topBar = {
            Column {
                MockStatusBar()
                ScreenTitle("统计图表")
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
            // 顶部收支总览
            OverviewSummaryCard(totalIncome = 12500.75, totalExpense = 7850.20)

            Spacer(modifier = Modifier.height(24.dp))

            // 支出分类饼图占位
            ChartPlaceholderCard(title = "支出分类占比", chartType = "饼图", icon = Icons.Filled.PieChart)

            Spacer(modifier = Modifier.height(16.dp))

            // 收支趋势柱状图占位
            ChartPlaceholderCard(title = "收支趋势", chartType = "柱状图", icon = Icons.Filled.BarChart)

            Spacer(modifier = Modifier.height(16.dp))

            // 更多图表占位
            ChartPlaceholderCard(title = "月度对比", chartType = "折线图", icon = Icons.Filled.DonutLarge)
        }
    }
}

@Composable
fun OverviewSummaryCard(totalIncome: Double, totalExpense: Double) {
    val balance = totalIncome - totalExpense
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("本月收支概览", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SummaryItem("总收入", "%.2f".format(totalIncome), Color(0xFF43A047)) // 绿色
                SummaryItem("总支出", "%.2f".format(totalExpense), Color(0xFFE53935)) // 红色
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider() // 重命名为 HorizontalDivider
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("结余: ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    "¥%.2f".format(balance),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (balance >= 0) Color(0xFF43A047) else Color(0xFFE53935)
                )
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "¥$value",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

@Composable
fun ChartPlaceholderCard(title: String, chartType: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = title, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp) // 模拟图表区域高度
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text("这是一个 $chartType 占位符", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOverviewStatsScreen() {
    JzTheme {
        OverviewStatsScreen()
    }
}