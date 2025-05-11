package com.jizhang.ak.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.* // 导入 AutoMirrored 版本下的所有图标
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
// import com.jizhang.ak.viewmodel.AuthViewModel // 移除 AuthViewModel 导入
import com.jizhang.ak.ui.theme.JzTheme
import kotlinx.coroutines.launch

data class SettingItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun SettingsScreen() { // 移除 authViewModel 参数
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope() // coroutineScope 可能不再需要，除非有其他异步操作
    val settingsItems = listOf(
        // SettingItem("账户管理", Icons.Filled.ManageAccounts) { Toast.makeText(context, "账户管理功能待实现", Toast.LENGTH_SHORT).show() }, // 移除账户管理
        SettingItem("分类管理", Icons.Filled.Category) { Toast.makeText(context, "分类管理功能待实现", Toast.LENGTH_SHORT).show() },
        SettingItem("数据导出", Icons.Filled.UploadFile) { Toast.makeText(context, "数据导出功能待实现", Toast.LENGTH_SHORT).show() },
        SettingItem("数据同步", Icons.Filled.Sync) { Toast.makeText(context, "数据同步功能待实现", Toast.LENGTH_SHORT).show() },
        SettingItem("外观设置", Icons.Filled.Palette) { Toast.makeText(context, "外观设置功能待实现", Toast.LENGTH_SHORT).show() },
        SettingItem("提醒设置", Icons.Filled.Notifications) { Toast.makeText(context, "提醒设置功能待实现", Toast.LENGTH_SHORT).show() },
        SettingItem("关于我们", Icons.Filled.Info) { Toast.makeText(context, "关于我们功能待实现", Toast.LENGTH_SHORT).show() },
        SettingItem("检查更新", Icons.Filled.SystemUpdate) { Toast.makeText(context, "检查更新功能待实现", Toast.LENGTH_SHORT).show() },
        SettingItem("帮助与反馈", Icons.AutoMirrored.Filled.Help) { Toast.makeText(context, "帮助与反馈功能待实现", Toast.LENGTH_SHORT).show() }
        // Logout button will be added separately
    )

    Scaffold(
        // topBar is removed
    ) { paddingValues ->
        Column( // Wrap content in a Column for title and list
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background) // Apply background to the main column
                .padding(paddingValues)
                // .padding(horizontal = 16.dp) // Settings items have their own padding, so maybe not needed here
        ) {
            // Mock Status Bar (if needed)
            // MockStatusBar()

            Spacer(modifier = Modifier.height(16.dp)) // Space from top

            // Title
            Text(
                text = "设置",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp).align(Alignment.Start) // Add start padding for title
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                // .background(MaterialTheme.colorScheme.background) // Background is on parent
            ) {
                items(settingsItems) { item ->
                    SettingRow(item = item)
                    HorizontalDivider(modifier = Modifier.padding(start = 72.dp)) // Keep divider as is or adjust
                }
                // item { // 移除退出登录按钮
                //     Spacer(modifier = Modifier.height(16.dp))
                //     Button(
            //         onClick = {
            //             coroutineScope.launch {
            //                 // authViewModel.logout() // authViewModel 已移除
            //                 // Navigation back to LoginScreen is handled by AppNavigation reacting to isLoggedIn state change
            //             }
            //         },
            //         modifier = Modifier
            //             .fillMaxWidth()
            //             .padding(horizontal = 16.dp),
            //         colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            //     ) {
            //         Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
            //         Spacer(modifier = Modifier.width(8.dp))
            //         Text("退出登录")
            //     }
            //     Spacer(modifier = Modifier.height(16.dp))
            // }
        }
    }
}
}

@Composable
fun SettingRow(item: SettingItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            item.icon,
            contentDescription = item.title,
            modifier = Modifier.size(28.dp), // Slightly larger icon for settings
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(28.dp)) // Increased spacing
        Text(item.title, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Filled.ChevronRight, contentDescription = "Navigate", tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    JzTheme {
        SettingsScreen()
    }
}