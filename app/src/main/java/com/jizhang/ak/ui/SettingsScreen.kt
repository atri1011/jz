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
import com.jizhang.ak.viewmodel.AuthViewModel
import com.jizhang.ak.ui.theme.JzTheme
import kotlinx.coroutines.launch

data class SettingItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun SettingsScreen(authViewModel: AuthViewModel = viewModel()) { // 接收 AuthViewModel
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val settingsItems = listOf(
        SettingItem("账户管理", Icons.Filled.ManageAccounts) { Toast.makeText(context, "账户管理功能待实现", Toast.LENGTH_SHORT).show() },
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
        topBar = {
            Column {
                MockStatusBar()
                ScreenTitle("设置")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(settingsItems) { item ->
                SettingRow(item = item)
                HorizontalDivider(modifier = Modifier.padding(start = 72.dp))
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            authViewModel.logout()
                            // Navigation back to LoginScreen is handled by AppNavigation reacting to isLoggedIn state change
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout") // 尝试保留 AutoMirrored，因为导入已修复
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("退出登录")
                }
                Spacer(modifier = Modifier.height(16.dp))
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