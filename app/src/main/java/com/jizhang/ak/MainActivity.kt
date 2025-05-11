package com.jizhang.ak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager // 新增
import androidx.compose.foundation.pager.rememberPagerState // 新增
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.automirrored.filled.List // 更新导入
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope // 新增
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.navigation.NavHostController // 添加导入
import com.jizhang.ak.data.AppDatabase
import com.jizhang.ak.ui.*
import kotlinx.coroutines.launch
import com.jizhang.ak.ui.theme.JzTheme
// import com.jizhang.ak.viewmodel.AuthViewModel // 移除 AuthViewModel 导入
import com.jizhang.ak.viewmodel.TransactionViewModel
import com.jizhang.ak.viewmodel.TransactionViewModelFactory
import androidx.compose.runtime.collectAsState // 新增

sealed class Screen(val route: String, val label: String? = null, val icon: ImageVector? = null) {
    object TransactionList : Screen("transaction_list", "流水", Icons.AutoMirrored.Filled.List)
    object OverviewStats : Screen("overview_stats", "图表", Icons.Filled.PieChart)
    object AddTransaction : Screen("add_transaction", "记账", Icons.Filled.AddCircle)
    object Settings : Screen("settings", "设置", Icons.Filled.Settings)
    // object Login : Screen("login") // 移除 Login Screen
    // object Register : Screen("register") // 移除 Register Screen
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
                AppNavigation() // 改为调用 AppNavigation
            }
        }
    }
}

@Composable
fun AppNavigation() { // 移除 authViewModel
    // val isLoggedIn by authViewModel.isLoggedIn.collectAsState() // 移除
    // val navController = rememberNavController() // 移除，如果 AuthNavHost 被移除

    // if (isLoggedIn) { // 移除条件判断
    MainAppScreen() // 直接调用 MainAppScreen，不再传递 authViewModel
    // } else {
    //     AuthNavHost(navController = navController, authViewModel = authViewModel) // 移除 AuthNavHost 调用
    // }
}

// @Composable // 移除整个 AuthNavHost 函数
// fun AuthNavHost(navController: NavHostController, authViewModel: AuthViewModel) {
//     NavHost(navController = navController, startDestination = Screen.Login.route) {
//         composable(Screen.Login.route) {
//             LoginScreen(
//                 authViewModel = authViewModel,
//                 onLoginSuccess = {
//                     // 登录成功后，isLoggedIn 状态会更新，AppNavigation 会自动切换到 MainAppScreen
//                 },
//                 onNavigateToRegister = { navController.navigate(Screen.Register.route) }
//             )
//         }
//         composable(Screen.Register.route) {
//             RegisterScreen(
//                 authViewModel = authViewModel,
//                 onRegisterSuccess = {
//                     // 注册成功后，isLoggedIn 状态会更新，AppNavigation 会自动切换到 MainAppScreen
//                 },
//                 onNavigateToLogin = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Login.route) { inclusive = true } } }
//             )
//         }
//     }
// }


@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun MainAppScreen() { // 移除 authViewModel 参数
    val mainNavController = rememberNavController() // MainAppScreen 内部的 NavController，如果 AddTransactionScreen 等需要
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context.applicationContext)
    val transactionDao = db.transactionDao()
    val transactionViewModel: TransactionViewModel = viewModel(factory = TransactionViewModelFactory(transactionDao))

    val pagerScreens = bottomNavItems
    val pagerState = rememberPagerState { pagerScreens.size }
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            NavigationBar {
                pagerScreens.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon!!, contentDescription = screen.label) }, // 使用 !! 因为 bottomNavItems 中的 screen 都有 icon 和 label
                        label = { Text(screen.label!!) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(innerPadding)
        ) { pageIndex ->
            when (val currentScreen = pagerScreens[pageIndex]) {
                Screen.TransactionList -> TransactionListScreen(transactionViewModel = transactionViewModel)
                Screen.OverviewStats -> OverviewStatsScreen(viewModel = transactionViewModel)
                Screen.AddTransaction -> AddTransactionScreen(navController = mainNavController, transactionViewModel = transactionViewModel)
                Screen.Settings -> SettingsScreen() // 不再传递 authViewModel
                else -> { /* 处理其他可能的 Screen 类型，或者如果 pagerScreens 只包含这四个，则不需要 else */ }
            }
        }
    }
}

// @Preview(showBackground = true) // 移除预览
// @Composable
// fun DefaultPreviewAuthScreens() {
//     JzTheme {
//         // 预览 AuthNavHost 可能需要一个 mock NavController 和 ViewModel
//         // 为了简单起见，这里可以预览 LoginScreen
//         // LoginScreen(onLoginSuccess = {}, onNavigateToRegister = {}) // LoginScreen 已删除
//     }
// }