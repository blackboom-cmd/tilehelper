<script setup>
import { useRouter } from 'vue-router'
import { useTheme } from '../composables/useTheme'

const router = useRouter()
const { themeColor } = useTheme()

const currentUser = localStorage.getItem('currentUser') || ''

const menuItems = [
  { name: '首页', path: '/' },
  { name: '瓦片信息表', path: '/tile' },
  { name: '订单表', path: '/order' },
  { name: '运输表', path: '/transport' },
  { name: '货车表', path: '/truck' },
  { name: '设置', path: '/setting' }
]

const navigate = (path) => {
  router.push(path)
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('currentUser')
  router.push('/login')
}
</script>

<template>
  <div class="sidebar" :style="{ backgroundColor: themeColor }">
    <h2 class="logo">瓦片助手</h2>
    <ul class="menu">
      <li v-for="item in menuItems" :key="item.path">
        <a href="#" @click.prevent="navigate(item.path)">{{ item.name }}</a>
      </li>
    </ul>

    <div class="sidebar-footer">
      <div class="user-info">
        <span class="user-icon">&#9679;</span>
        <span class="username">{{ currentUser }}</span>
      </div>
      <a href="#" class="logout-btn" @click.prevent="handleLogout">退出登录</a>
    </div>
  </div>
</template>

<style scoped>
.sidebar {
  width: 200px;
  flex-shrink: 0;

  background-color: #f5f5f5;
  height: 100%;
  border-right: 1px solid #ddd;
  padding: 20px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}
.logo {
  margin-bottom: 30px;
  text-align: center;
  font-size: 1.5rem;
}
.menu {
  list-style: none;
  padding: 0;
  flex: 1;
}
.menu li {
  margin-bottom: 15px;
}
.menu li a {
  display: block;
  padding: 10px;
  text-decoration: none;
  color: #333;
  border-radius: 4px;
  transition: background-color 0.3s;
}
.menu li a:hover {
  background-color: #e0e0e0;
}

.sidebar-footer {
  border-top: 1px solid rgba(0, 0, 0, 0.1);
  padding-top: 16px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  padding: 0 4px;
}
.user-icon {
  color: #4caf50;
  font-size: 12px;
}
.username {
  color: #333;
  font-size: 14px;
}
.logout-btn {
  display: block;
  padding: 10px;
  text-decoration: none;
  color: #e74c3c;
  border-radius: 4px;
  transition: background-color 0.3s;
  font-size: 14px;
}
.logout-btn:hover {
  background-color: rgba(231, 76, 60, 0.1);
}
</style>
