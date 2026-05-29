<template>
  <div class="login-page" :style="{ backgroundColor: contentBgColor }">
    <div class="login-card">
      <h2>瓦片助手</h2>
      <p class="subtitle">登录您的账户</p>

      <div class="form-group">
        <label for="username">用户名</label>
        <input
          id="username"
          v-model="username"
          type="text"
          placeholder="请输入用户名"
          :disabled="lockSeconds > 0"
          @keyup.enter="handleLogin"
        />
      </div>

      <div class="form-group">
        <label for="password">密码</label>
        <input
          id="password"
          v-model="password"
          type="password"
          placeholder="请输入密码"
          :disabled="lockSeconds > 0"
          @keyup.enter="handleLogin"
        />
      </div>

      <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>

      <button class="login-btn" :disabled="loading || lockSeconds > 0" @click="handleLogin">
        {{ lockSeconds > 0 ? lockSeconds + '秒后可登录' : loading ? '登录中...' : '登 录' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useTheme } from '../composables/useTheme'
import { login } from '../api/auth'

const router = useRouter()
const { contentBgColor } = useTheme()

const username = ref('')
const password = ref('')
const loading = ref(false)
const errorMsg = ref('')
const lockSeconds = ref(0)
let countdownTimer = null

function startCountdown(seconds) {
  clearCountdown()
  lockSeconds.value = seconds
  updateLockMsg()
  countdownTimer = setInterval(() => {
    lockSeconds.value--
    if (lockSeconds.value <= 0) {
      clearCountdown()
      errorMsg.value = ''
    } else {
      updateLockMsg()
    }
  }, 1000)
}

function updateLockMsg() {
  errorMsg.value = '账户已被锁定，请' + lockSeconds.value + '秒后再试'
}

function clearCountdown() {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
  lockSeconds.value = 0
}

onUnmounted(() => {
  clearCountdown()
})

async function handleLogin() {
  errorMsg.value = ''

  if (!username.value.trim()) {
    errorMsg.value = '请输入用户名'
    return
  }
  if (!password.value) {
    errorMsg.value = '请输入密码'
    return
  }

  loading.value = true
  try {
    const res = await login(username.value.trim(), password.value)
    if (res.data.code === 200) {
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('currentUser', res.data.username)
      router.replace('/')
    } else {
      errorMsg.value = res.data.message || '登录失败'
    }
  } catch (e) {
    const msg = e.response?.data?.message || e.response?.data?.error || '登录失败，请检查网络连接'

    const secondsMatch = msg.match(/(\d+)秒后再试/)
    const minutesMatch = msg.match(/已被锁定(\d+)分钟/)

    if (secondsMatch) {
      startCountdown(parseInt(secondsMatch[1]))
    } else if (minutesMatch) {
      startCountdown(parseInt(minutesMatch[1]) * 60)
    } else {
      errorMsg.value = msg
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100vw;
  height: 100vh;
}

.login-card {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  width: 380px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.1);
}

.login-card h2 {
  text-align: center;
  color: #333;
  margin-bottom: 8px;
  font-size: 24px;
}

.subtitle {
  text-align: center;
  color: #999;
  margin-bottom: 32px;
  font-size: 14px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  color: #555;
  font-size: 14px;
}

.form-group input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  box-sizing: border-box;
  transition: border-color 0.2s;
}

.form-group input:focus {
  border-color: #4a90d9;
  box-shadow: 0 0 0 2px rgba(74, 144, 217, 0.15);
}

.form-group input:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
}

.error-msg {
  color: #e74c3c;
  font-size: 13px;
  margin-bottom: 16px;
  text-align: center;
  padding: 8px;
  background: #fef0ef;
  border-radius: 4px;
}

.login-btn {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 6px;
  background: #4a90d9;
  color: #fff;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.login-btn:hover {
  background: #357abd;
}

.login-btn:disabled {
  background: #a0c4e8;
  cursor: not-allowed;
}
</style>
