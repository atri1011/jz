# 个人记账 App (AI 协助开发)

## 项目简介

这是一款用于个人财务记录和管理的 Android 应用。它帮助用户轻松跟踪日常收入和支出，提供清晰的财务概览。本项目为纯本地应用，所有数据均安全存储在用户设备上。

## AI 协助声明

本项目是在 AI 助手 Roo 的协助下完成开发的，展示了 AI 在软件开发中的协作潜力，包括代码生成、问题解答、功能规划和文档编写等方面。

## 主要功能列表

*   **交易记录**：快速、便捷地记录每笔收入和支出。
*   **交易流水**：按时间顺序清晰展示所有交易记录，支持按月份筛选查看。
*   **统计概览**：实时显示总收入、总支出以及当前结余。
*   **支出分类饼图**：通过饼图直观展示不同支出类别的占比情况。
*   **收支趋势图**：以柱状图形式展示每日的收入与支出对比，帮助分析消费习惯。
*   **分类管理**：支持查看、添加、编辑和删除收支分类，方便用户自定义管理。
*   **本地数据存储**：采用 Room 数据库在本地持久化存储所有交易数据，确保数据安全和隐私。
*   **流畅的 UI 体验**：基于 Jetpack Compose 构建现代化用户界面，支持底部 Tab 平滑滑动切换页面。

## 技术栈

*   **语言**：Kotlin
*   **UI**：Jetpack Compose
*   **架构**：MVVM (Model-View-ViewModel)
*   **数据持久化**：Room Persistence Library
*   **异步处理**：Kotlin Coroutines, Flow
*   **导航**：Jetpack Compose Navigation (以及 `HorizontalPager` 用于主 Tab 切换)
*   **原型设计**：HTML, Tailwind CSS (用于项目早期的快速原型验证)

## 项目结构 (简要)

*   `app/src/main/java/com/jizhang/ak/data`：数据层，包含 Room 数据库实体、DAO、数据库实例以及可能的 DTOs。
*   `app/src/main/java/com/jizhang/ak/ui`：UI 层，包含所有 Jetpack Compose 编写的屏幕 (Screens) 和应用主题 (Theme)。
*   `app/src/main/java/com/jizhang/ak/viewmodel`：ViewModel 层，负责处理业务逻辑并为 UI 提供数据。

## 如何运行 (简要)

1.  在 Android Studio 中打开本项目。
2.  选择一个模拟器或连接一台真实的 Android 设备。
3.  点击 "Run"按钮构建并运行应用。