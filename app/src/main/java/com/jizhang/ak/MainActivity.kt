package com.jizhang.ak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.automirrored.filled.List // 更新导入
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.jizhang.ak.ui.*
import com.jizhang.ak.ui.theme.JzTheme
import com.jizhang.ak.viewmodel.TransactionViewModel // 导入 ViewModel

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object TransactionList : Screen("transaction_list", "流水", Icons.AutoMirrored.Filled.List)
    object OverviewStats : Screen("overview_stats", "图表", Icons.Filled.PieChart)
    object AddTransaction : Screen("add_transaction", "记账", Icons.Filled.AddCircle)
    object Settings : Screen("settings", "设置", Icons.Filled.Settings)
}

val bottomNavItems = listOf(
    Screen.TransactionList,
    Screen.OverviewStats,
    Screen.AddTransaction,
    Screen.Settings
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JzTheme {
                MainAppScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {
    val navController = rememberNavController()
    // ViewModel 在 NavHost 级别创建，以便共享
    val transactionViewModel: TransactionViewModel = viewModel()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.TransactionList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.TransactionList.route) {
                TransactionListScreen(transactionViewModel = transactionViewModel)
            }
            composable(Screen.OverviewStats.route) { OverviewStatsScreen() } // ViewModel 暂不传递
            composable(Screen.AddTransaction.route) {
                AddTransactionScreen(navController = navController, transactionViewModel = transactionViewModel)
            }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewMainAppScreen() {
    JzTheme {
        MainAppScreen()
    }
}