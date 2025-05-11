package com.jizhang.ak.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help // 导入 AutoMirrored 版本
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jizhang.ak.ui.theme.JzTheme

data class SettingItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
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
                HorizontalDivider(modifier = Modifier.padding(start = 72.dp)) // 重命名为 HorizontalDivider
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