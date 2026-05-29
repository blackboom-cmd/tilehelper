# TileHelper 瓦片管理系统

## 📌 项目简介

TileHelper 是一个基于 SpringBoot + Vue3 + Vite 的瓦片资源管理系统，用于实现瓦片图片的上传、存储与展示功能，适用于地图瓦片或图片资源管理场景。

---

## 🛠 技术栈

后端：SpringBoot  
前端：Vue3 + Vite  
数据库：MySQL  
构建工具：Maven / npm

---

## 🚀 项目运行步骤

### 1️⃣ 克隆项目

git clone https://github.com/blackboom-cmd/tilehelper.git  
cd tilehelper

---

### 2️⃣ 数据库导入

使用 Navicat 或 MySQL 执行项目中的 SQL 文件（tileassistant.sql ）。

步骤如下：  
打开 MySQL → 创建数据库（如 tileassistant）→ 导入 SQL 文件 → 执行完成数据与表结构初始化。

---

### 3️⃣ 启动后端（SpringBoot）

在项目根目录执行：

mvn spring-boot:run

或在 IDEA 中直接运行主启动类（Application）。

---

### 4️⃣ 启动前端（Vue）

进入前端目录：

cd frontend

安装依赖：

npm install

启动项目：

npm run dev

---

### 5️⃣ 访问项目

前端默认访问地址：

http://localhost:5173

---

## 📂 项目结构说明

tilehelper/  
├── src/              后端代码  
├── frontend/         前端代码  
├── database.sql      数据库文件  
├── pom.xml           Maven配置  
├── mvnw  
├── mvnw.cmd

---

## 📌 功能说明

瓦片图片上传与管理  
瓦片资源展示  
前后端分离架构  
数据库统一存储管理

---

## ⚠️ 注意事项

请确保 MySQL 服务已启动  
前端必须先执行 npm install  
后端默认端口 8080  
前端默认端口 5173  
如果端口冲突，请修改 vite.config.js 或 SpringBoot 配置

---

## 👨‍💻 作者

blackboom-cmd

---

## 📜 License

MIT License