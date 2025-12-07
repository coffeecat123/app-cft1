# CFT1

[![GitHub tag](https://img.shields.io/github/v/tag/coffeecat123/app-cft1?label=Latest%20Release)](https://github.com/coffeecat123/app-cft1/releases)

## 🐈 專案介紹 (Project Overview)

**CFT1** 是一個簡單的 Android 待辦事項應用，使用 **Jetpack Compose** 開發。  
它提供基本的待辦事項管理功能，並支援通知提醒，方便使用者隨時查看待辦清單。

本專案適合想要快速管理日常任務的使用者，或學習 Compose 與通知系統的開發者。

## ✨ 功能特點 (Features)

* **新增待辦事項 (Add Task)**: 輕鬆新增待辦項目
* **刪除待辦事項 (Delete Task)**: 支援單個或多個待辦項目刪除
* **本地存儲 (Local Storage)**: 使用 `SharedPreferences` 儲存待辦清單
* **通知提醒 (Notification)**: 在通知欄顯示待辦清單
    * Android 13+ 需授權通知權限

## 🛠️ 技術細節 (Technical Details)

| 類別 | 細節 |
| :--- | :--- |
| **主要語言** | Kotlin |
| **UI 工具包** | Jetpack Compose |
| **資料儲存** | SharedPreferences |
| **通知系統** | NotificationManager (支援 Android 13+ 通知權限) |
| **支援版本** | Android 8.0 (API 26) 及以上 |

## 🚀 安裝與執行 (Installation)

### 系統要求

* Android 8.0 (API 26) 或更高版本
* Android Studio (建議使用最新穩定版本)

### 開發者設定

1. **複製倉庫 (Clone the repository)**
    ```bash
    git clone https://github.com/coffeecat123/app-cft1.git
    ```

2. **開啟專案**
   在 Android Studio 中打開專案資料夾。

3. **建置與執行**
   等待 Gradle 同步完成後，即可在連接的 Android 裝置或模擬器上執行應用程式。

## 📖 使用指南 (Usage)

### 1. 新增待辦事項
1. 打開 App 主畫面
2. 點擊 **新增按鈕**
3. 輸入待辦事項內容，點擊確認

### 2. 刪除待辦事項
1. 在待辦清單中滑動或點擊刪除按鈕
2. 待辦事項將從清單與本地存儲中移除

### 3. 通知提醒
* 開啟通知權限後，待辦清單會在通知欄顯示，方便快速查看與提醒。

## 🤝 貢獻指南 (Contributing)

歡迎任何形式的貢獻！您可以：

1. **Fork** 本倉庫
2. 建立功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交修改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 開啟 **Pull Request (PR)**

## 📄 授權條款 (License)

本專案採用 **MIT 授權條款**。詳情請見 [LICENSE](LICENSE) 檔案。

Copyright (c) 2025 coffeecat123

---

## 謝誌 (Acknowledgements)

* **Jetpack Compose** - 現代化 Android UI 框架
* **SharedPreferences** - 本地資料儲存簡單方案
* **NotificationManager** - Android 通知系統

> （註：部分文件內容可由 AI 生成）
