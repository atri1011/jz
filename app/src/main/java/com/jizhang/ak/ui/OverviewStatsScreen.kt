package com.jizhang.ak.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
// import androidx.compose.material.icons.filled.DonutLarge // Not used for pie chart
// import androidx.compose.material.icons.filled.PieChart // Not used directly, but relevant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jizhang.ak.viewmodel.TransactionViewModel
import com.jizhang.ak.ui.theme.JzTheme
// import androidx.compose.foundation.lazy.LazyColumn // Not used in this modification
// import androidx.compose.foundation.lazy.items // Not used in this modification

@Composable
fun OverviewStatsScreen(viewModel: TransactionViewModel = viewModel()) {
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val expensesByCategory by viewModel.expensesByCategory.collectAsState()

    Scaffold(
        topBar = {
            Column {
                MockStatusBar() // Assuming this is a custom composable for status bar padding
                ScreenTitle("统计图表") // Assuming this is a custom composable for screen title
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
            OverviewSummaryCard(
                totalIncome = totalIncome,
                totalExpense = totalExpenses,
                balance = balance
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 支出分类列表
            ExpensesByCategoryCard(expensesByCategory = expensesByCategory, pieChartColors = pieChartColors)

            Spacer(modifier = Modifier.height(16.dp))

            // 收支趋势图占位
            ChartPlaceholderCard(title = "收支趋势", chartType = "柱状图", icon = Icons.Filled.BarChart, placeholderText = "收支趋势图待实现")

            //Spacer(modifier = Modifier.height(16.dp)) // Removed extra chart placeholder for now
            // ChartPlaceholderCard(title = "月度对比", chartType = "折线图", icon = Icons.Filled.DonutLarge)
        }
    }
}

@Composable
fun OverviewSummaryCard(totalIncome: Double, totalExpense: Double, balance: Double) {
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
            Text("收支总览", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SummaryItem("总收入", totalIncome, Color(0xFF4CAF50)) // Green
                SummaryItem("总支出", totalExpense, Color(0xFFF44336)) // Red
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("结余: ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    "¥${String.format("%.2f", balance)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (balance >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, amount: Double, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "¥${String.format("%.2f", amount)}",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

// Define colors for the pie chart, can be moved to a theme or constants file later
val pieChartColors = listOf(
    Color(0xFFF44336), Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7),
    Color(0xFF3F51B5), Color(0xFF2196F3), Color(0xFF03A9F4), Color(0xFF00BCD4),
    Color(0xFF009688), Color(0xFF4CAF50), Color(0xFF8BC34A), Color(0xFFCDDC39),
    Color(0xFFFFEB3B), Color(0xFFFFC107), Color(0xFFFF9800), Color(0xFFFF5722)
)

@Composable
fun SimplePieChart(
    data: Map<String, Double>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp), // Same height as chart for consistency
            contentAlignment = Alignment.Center
        ) {
            Text("暂无支出数据以生成饼图", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    val totalValue = data.values.sum()

    if (totalValue == 0.0) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp), // Same height as chart for consistency
            contentAlignment = Alignment.Center
        ) {
            Text("支出总额为零或无有效数据", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    var startAngle = 0f

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val radius = minOf(canvasWidth, canvasHeight) / 2f
        val chartSize = Size(radius * 2, radius * 2)
        val topLeftX = (canvasWidth - chartSize.width) / 2f
        val topLeftY = (canvasHeight - chartSize.height) / 2f

        data.entries.forEachIndexed { index, entry ->
            // Ensure proportion is not NaN or Infinity if entry.value is 0 and totalValue is also 0 (already handled by totalValue check)
            // or if entry.value is non-zero but totalValue is extremely small.
            // However, the primary check for totalValue == 0.0 should cover most cases.
            val proportion = if (totalValue > 0) (entry.value / totalValue).toFloat() else 0f
            val sweepAngle = 360f * proportion
            val color = colors[index % colors.size]

            if (sweepAngle > 0f) { // Only draw if there's something to draw
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = androidx.compose.ui.geometry.Offset(topLeftX, topLeftY),
                    size = chartSize
                )
            }
            startAngle += sweepAngle
        }
    }
}


@Composable
fun ExpensesByCategoryCard(
    expensesByCategory: Map<String, Double>,
    pieChartColors: List<Color>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("支出分类", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(16.dp))

            if (expensesByCategory.isEmpty()) {
                SimplePieChart(
                    data = expensesByCategory,
                    colors = pieChartColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // Provide a default height for the message
                )
                Spacer(modifier = Modifier.height(12.dp)) // Space between chart placeholder and message
                Text("暂无支出数据", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                SimplePieChart(
                    data = expensesByCategory,
                    colors = pieChartColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // Adjust size as needed
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Legend
                Column {
                    expensesByCategory.entries.forEachIndexed { index, entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(pieChartColors[index % pieChartColors.size])
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(entry.key, fontSize = 16.sp, modifier = Modifier.weight(1f))
                            Text("¥${String.format("%.2f", entry.value)}", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ChartPlaceholderCard(title: String, chartType: String, icon: androidx.compose.ui.graphics.vector.ImageVector, placeholderText: String) {
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
                    .height(150.dp) // Simulate chart area height
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(placeholderText, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOverviewStatsScreen() {
    // For preview, we can pass a mock ViewModel or use default values.
    // Here, we'll rely on the default ViewModel which will have empty initial states.
    JzTheme {
        OverviewStatsScreen()
    }
}