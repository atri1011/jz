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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jizhang.ak.viewmodel.TransactionViewModel
import com.jizhang.ak.viewmodel.DailyNetIncome // Import the data class
import com.jizhang.ak.viewmodel.DailyFinancialSummary // Import the new data class
import com.jizhang.ak.ui.theme.JzTheme
// import androidx.compose.foundation.lazy.LazyColumn // Not used in this modification
// import androidx.compose.foundation.lazy.items // Not used in this modification
import kotlin.math.abs
import kotlin.math.max

@Composable
fun OverviewStatsScreen(viewModel: TransactionViewModel = viewModel()) {
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val expensesByCategory by viewModel.expensesByCategory.collectAsState()
    val dailyNetIncomeData by viewModel.dailyNetIncomeFlow.collectAsState() // Keep for now, might remove if not used elsewhere
    val dailySummaries by viewModel.dailySummariesFlow.collectAsState()

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

            // 收支趋势图
            // DailyNetIncomeBarChartCard(dailyNetIncomeData = dailyNetIncomeData) // Replaced by EnhancedDailyBarChartCard
            EnhancedDailyBarChartCard(summaries = dailySummaries)


            //Spacer(modifier = Modifier.height(16.dp)) // Removed extra chart placeholder for now
            // ChartPlaceholderCard(title = "月度对比", chartType = "折线图", icon = Icons.Filled.DonutLarge)
        }
    }
}

@Composable
fun EnhancedDailyBarChartCard(summaries: List<DailyFinancialSummary>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.BarChart, contentDescription = "收支趋势", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("收支趋势", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (summaries.isEmpty() || summaries.size < 2) { // Require at least a couple of days for a meaningful chart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp) // Adjusted height for the new chart
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("暂无足够数据生成趋势图", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                EnhancedDailyBarChart(
                    summaries = summaries,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp) // Adjusted height
                )
            }
        }
    }
}

@Composable
fun EnhancedDailyBarChart(
    summaries: List<DailyFinancialSummary>,
    modifier: Modifier = Modifier,
    incomeColor: Color = Color(0xFF4CAF50), // Green for income
    expenseColor: Color = Color(0xFFF44336) // Red for expense
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    Canvas(modifier = modifier) {
        if (summaries.isEmpty()) return@Canvas

        val canvasWidth = size.width
        val canvasHeight = size.height

        // --- Margins and Paddings ---
        val legendHeight = 30.dp.toPx()
        val xAxisLabelHeight = 20.dp.toPx()
        val yAxisLabelWidth = 40.dp.toPx() // Space for Y-axis labels
        val topPadding = 10.dp.toPx()
        val bottomPadding = 5.dp.toPx() // Padding below x-axis labels
        val rightPadding = 10.dp.toPx()

        val chartAreaHeight = canvasHeight - legendHeight - xAxisLabelHeight - topPadding - bottomPadding
        val chartAreaWidth = canvasWidth - yAxisLabelWidth - rightPadding

        // --- Legend ---
        val legendDotSize = 8.dp.toPx()
        val legendTextOffsetY = legendDotSize * 0.15f // Slight adjustment for text alignment
        val legendSpacing = 10.dp.toPx()

        // Income legend
        drawCircle(
            color = incomeColor,
            radius = legendDotSize / 2,
            center = Offset(yAxisLabelWidth + legendDotSize / 2, topPadding + legendDotSize / 2)
        )
        drawText(
            textMeasurer = textMeasurer,
            text = "收入",
            topLeft = Offset(yAxisLabelWidth + legendDotSize + legendSpacing / 2, topPadding + legendTextOffsetY),
            style = TextStyle(color = Color.Black, fontSize = 12.sp)
        )

        // Expense legend (position it after income legend text)
        val incomeTextLayoutResult = textMeasurer.measure("收入", style = TextStyle(fontSize = 12.sp))
        val expenseLegendXStart = yAxisLabelWidth + legendDotSize + legendSpacing / 2 + incomeTextLayoutResult.size.width + legendSpacing * 2

        drawCircle(
            color = expenseColor,
            radius = legendDotSize / 2,
            center = Offset(expenseLegendXStart + legendDotSize / 2, topPadding + legendDotSize / 2)
        )
        drawText(
            textMeasurer = textMeasurer,
            text = "支出",
            topLeft = Offset(expenseLegendXStart + legendDotSize + legendSpacing / 2, topPadding + legendTextOffsetY),
            style = TextStyle(color = Color.Black, fontSize = 12.sp)
        )

        // --- Y-Axis ---
        val maxIncomeOrExpense = summaries.maxOfOrNull { max(it.totalIncome, it.totalExpenses) } ?: 0.0
        val yAxisMaxValue = if (maxIncomeOrExpense == 0.0) 100.0 else maxIncomeOrExpense // Avoid division by zero, provide a default scale

        val yAxisStart = Offset(yAxisLabelWidth, legendHeight + topPadding)
        val yAxisEnd = Offset(yAxisLabelWidth, legendHeight + topPadding + chartAreaHeight)
        val xAxisStart = Offset(yAxisLabelWidth, legendHeight + topPadding + chartAreaHeight) // Baseline (0)
        val xAxisEnd = Offset(canvasWidth - rightPadding, legendHeight + topPadding + chartAreaHeight)

        // Draw Y-axis line
        drawLine(Color.Gray, yAxisStart, yAxisEnd, strokeWidth = 1.dp.toPx())
        // Draw X-axis baseline
        drawLine(Color.DarkGray, xAxisStart, xAxisEnd, strokeWidth = 1.5.dp.toPx())

        // Y-axis labels (0, Max/2, Max)
        val yLabelValues = listOf(0.0, yAxisMaxValue / 2, yAxisMaxValue)
        yLabelValues.forEach { value ->
            val yPos = xAxisStart.y - (value / yAxisMaxValue * chartAreaHeight).toFloat()
            val labelText = if (value > 1000) "${(value/1000).toInt()}k" else value.toInt().toString()
            val textLayout = textMeasurer.measure(labelText, style = TextStyle(fontSize = 10.sp, color = Color.Gray))
            drawText(
                textMeasurer = textMeasurer,
                text = labelText,
                topLeft = Offset(yAxisLabelWidth - textLayout.size.width - 4.dp.toPx(), yPos - textLayout.size.height / 2),
                style = TextStyle(fontSize = 10.sp, color = Color.Gray)
            )
            // Draw horizontal grid line
            drawLine(
                color = Color.LightGray.copy(alpha = 0.5f),
                start = Offset(yAxisLabelWidth, yPos),
                end = Offset(canvasWidth - rightPadding, yPos),
                strokeWidth = 0.5.dp.toPx()
            )
        }


        // --- X-Axis and Bars ---
        val numDays = summaries.size
        if (numDays == 0) return@Canvas

        val totalSpacingRatio = 0.4f // Proportion of space for gaps
        val groupWidth = chartAreaWidth / numDays
        val barAreaWidth = groupWidth * (1 - totalSpacingRatio) // Width for the two bars + their internal gap
        val barPairGap = groupWidth * totalSpacingRatio // Gap between pairs of bars

        val individualBarWidth = barAreaWidth / 2.2f // 2 bars, 0.2 for small gap between them
        val innerBarGap = barAreaWidth - (2 * individualBarWidth)


        summaries.forEachIndexed { index, summary ->
            val groupStartX = yAxisLabelWidth + index * groupWidth + barPairGap / 2

            // Income bar
            val incomeBarHeight = (summary.totalIncome / yAxisMaxValue * chartAreaHeight).toFloat().coerceAtLeast(0f)
            if (summary.totalIncome > 0) { // Only draw if there's income
                drawRect(
                    color = incomeColor,
                    topLeft = Offset(groupStartX, xAxisStart.y - incomeBarHeight),
                    size = Size(individualBarWidth, incomeBarHeight)
                )
            }

            // Expense bar
            val expenseBarHeight = (summary.totalExpenses / yAxisMaxValue * chartAreaHeight).toFloat().coerceAtLeast(0f)
            if (summary.totalExpenses > 0) { // Only draw if there's expense
                 drawRect(
                    color = expenseColor,
                    topLeft = Offset(groupStartX + individualBarWidth + innerBarGap, xAxisStart.y - expenseBarHeight),
                    size = Size(individualBarWidth, expenseBarHeight)
                )
            }

            // Date label
            val dateText = summary.date // "MM-dd"
            val dateTextLayout = textMeasurer.measure(dateText, style = TextStyle(fontSize = 10.sp, color = Color.DarkGray))
            drawText(
                textMeasurer = textMeasurer,
                text = dateText,
                topLeft = Offset(
                    groupStartX + barAreaWidth / 2 - dateTextLayout.size.width / 2,
                    xAxisStart.y + 4.dp.toPx()
                ),
                style = TextStyle(fontSize = 10.sp, color = Color.DarkGray)
            )
        }
    }
}


@Composable
fun SimpleBarChart(
    data: List<DailyNetIncome>,
    modifier: Modifier = Modifier,
    barColorPositive: Color = Color(0xFF4CAF50), // Green for income
    barColorNegative: Color = Color(0xFFF44336)  // Red for expense
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val spacing = 8.dp.toPx() // Spacing between bars
        val numberOfDays = data.size
        if (numberOfDays == 0) return@Canvas

        val barWidth = (canvasWidth - (spacing * (numberOfDays + 1))) / numberOfDays

        val maxNetAmount = data.maxOfOrNull { it.netAmount } ?: 0.0
        val minNetAmount = data.minOfOrNull { it.netAmount } ?: 0.0

        // Determine the overall range for scaling
        val overallMaxAbs = max(abs(maxNetAmount), abs(minNetAmount))
        if (overallMaxAbs == 0.0) { // All data is zero, draw a baseline and dates
            // Draw baseline
            val baselineY = canvasHeight / 2f
            drawLine(
                color = Color.Gray,
                start = Offset(0f, baselineY),
                end = Offset(canvasWidth, baselineY),
                strokeWidth = 1.dp.toPx()
            )
            // Draw date labels
            data.forEachIndexed { index, item ->
                val x = spacing + (barWidth + spacing) * index + barWidth / 2
                drawText(
                    textMeasurer = textMeasurer,
                    text = item.date.takeLast(5), // "MM-DD"
                    style = TextStyle(fontSize = 10.sp, color = Color.Gray),
                    topLeft = Offset(x - textMeasurer.measure(item.date.takeLast(5)).size.width / 2, baselineY + 4.dp.toPx())
                )
            }
            return@Canvas
        }


        // Calculate baseline position: if only positive or only negative, adjust baseline
        val baselineY = when {
            minNetAmount >= 0 -> canvasHeight * 0.9f // All positive, baseline near bottom
            maxNetAmount <= 0 -> canvasHeight * 0.1f // All negative, baseline near top
            else -> canvasHeight * (abs(maxNetAmount) / (abs(maxNetAmount) + abs(minNetAmount))).toFloat()
        }


        // Draw baseline (0 line)
        drawLine(
            color = Color.DarkGray,
            start = Offset(0f, baselineY),
            end = Offset(canvasWidth, baselineY),
            strokeWidth = 1.dp.toPx()
        )

        data.forEachIndexed { index, dailyNetIncome ->
            val barHeightFactor = if (overallMaxAbs != 0.0) dailyNetIncome.netAmount / overallMaxAbs else 0.0
            val barPixelHeight = abs(barHeightFactor * (if (minNetAmount >= 0 || maxNetAmount <= 0) canvasHeight * 0.8f else baselineY.coerceAtMost(canvasHeight - baselineY) * 0.9f )).toFloat()


            val barX = spacing + (barWidth + spacing) * index

            val top: Float
            val bottom: Float
            val color: Color

            if (dailyNetIncome.netAmount >= 0) {
                top = baselineY - barPixelHeight
                bottom = baselineY
                color = barColorPositive
            } else {
                top = baselineY
                bottom = baselineY + barPixelHeight
                color = barColorNegative
            }

            drawRect(
                color = color,
                topLeft = Offset(barX, top.coerceAtLeast(0f)), // Ensure top is not negative
                size = Size(barWidth, (bottom - top).coerceAtLeast(0f)) // Ensure height is not negative
            )

            // Optional: Draw date labels below bars
             drawText(
                 textMeasurer = textMeasurer,
                 text = dailyNetIncome.date.takeLast(5), // e.g., "05-11"
                 style = TextStyle(fontSize = 10.sp, color = Color.Gray),
                 topLeft = Offset(barX + barWidth / 2 - textMeasurer.measure(dailyNetIncome.date.takeLast(5)).size.width / 2, canvasHeight - 2.dp.toPx())
             )
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