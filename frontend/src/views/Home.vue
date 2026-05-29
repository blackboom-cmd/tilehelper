<template>
  <div class="home-page">
    <h1>首页</h1>
    <div class="sub-row">
      <span class="sub-title">展示首页信息</span>
    </div>

    <!-- ====== 上半部分：订单卡片(左) + 日历(右) ====== -->
    <div class="top-row">
      <!-- 左侧：订单卡片（可滚动） -->
      <div class="order-section">
        <div class="section-header-bar">
          <h2 class="link-title" @click="goOrderPage" title="前往订单表">订单概览 →</h2>
          <span class="section-count">{{ activeOrders.length }} 单进行中</span>
        </div>
        <div class="order-cards">
          <div
            v-for="order in activeOrders"
            :key="order.id"
            class="order-card"
            :class="{ selected: selectedOrderId === order.id }"
            :style="{
              borderLeft: `5px solid ${getDeliveryColor(order.deliveryAddress, order.deliveryTime)}`,
              background: selectedOrderId === order.id
                ? `linear-gradient(to right, ${getDeliveryColor(order.deliveryAddress, order.deliveryTime)}18, ${getDeliveryColor(order.deliveryAddress, order.deliveryTime)}08 40px)`
                : `linear-gradient(to right, ${getDeliveryColor(order.deliveryAddress, order.deliveryTime)}08, transparent 40px)`
            }"
            @click="selectOrder(order)"
          >
            <div class="card-top">
              <span class="status-badge" :style="{ background: statusColor(order.status) }">{{ order.status }}</span>
              <span class="customer-name">{{ order.customerName }}</span>
              <span class="amount">￥{{ order.totalAmount }}</span>
            </div>
            <div class="card-info-row">
              <span class="info-chip">{{ order.tileName || '—' }}</span>
              <span class="info-chip">{{ order.tileQuantity ?? '—' }} 张</span>
              <span class="info-chip" v-if="order.deliveryTime">{{ formatDate(order.deliveryTime) }}</span>
              <span class="info-chip addr" :title="order.deliveryAddress">{{ order.deliveryAddress || '—' }}</span>
            </div>
          </div>
          <div v-if="activeOrders.length === 0" class="empty">暂无进行中的订单</div>
        </div>
      </div>

      <!-- 右侧：日历 -->
      <div class="calendar-section">
        <div class="calendar-header">
          <button class="month-btn" @click="prevMonth">◀</button>
          <span class="month-title">{{ currentYear }}年 {{ currentMonth + 1 }}月</span>
          <button class="month-btn" @click="nextMonth">▶</button>
        </div>
        <div class="calendar-grid">
          <div class="day-header" v-for="d in dayHeaders" :key="d">{{ d }}</div>
          <div
            v-for="(day, idx) in calendarDays"
            :key="idx"
            class="day-cell"
            :class="{
              today: day.isToday,
              'other-month': !day.isCurrentMonth,
              'has-delivery': day.hasDelivery
            }"
            :style="day.hasDelivery
              ? { backgroundColor: day.deliveryColor + '20', borderColor: day.deliveryColor }
              : {}"
          >
            <span class="day-num" :style="day.hasDelivery ? { color: day.deliveryColor, fontWeight: '700' } : {}">{{ day.day }}</span>
            <div v-if="day.hasDelivery && day.deliveries.length > 0" class="delivery-dots">
              <span
                v-for="(dv, di) in day.deliveries.slice(0, 3)"
                :key="di"
                class="dot"
                :style="{ backgroundColor: dv.color }"
              ></span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ====== 下半部分：运输货车面板 ====== -->
    <div class="transport-section">
      <div class="section-header-bar">
        <h2 class="link-title" @click="goTransportPage" title="前往运输表">运输概览 →</h2>
        <span class="section-count" v-if="selectedOrder">关联订单 #{{ selectedOrder.id }}</span>
        <span class="section-count" v-else>{{ transports.length }} 条记录</span>
      </div>

      <div class="transport-panel">
        <!-- 左侧：货车图标 + 货物统计 -->
        <div class="truck-display">
          <svg class="truck-icon" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="4" y="22" width="40" height="28" rx="4" fill="#e5e7eb" stroke="#9ca3af" stroke-width="2"/>
            <rect x="44" y="14" width="16" height="36" rx="4" fill="#d1d5db" stroke="#9ca3af" stroke-width="2"/>
            <rect x="8" y="26" width="16" height="16" rx="2" fill="#f9fafb" stroke="#d1d5db" stroke-width="1"/>
            <circle cx="16" cy="54" r="7" fill="#fff" stroke="#6b7280" stroke-width="2.5"/>
            <circle cx="44" cy="54" r="7" fill="#fff" stroke="#6b7280" stroke-width="2.5"/>
            <line x1="20" y1="40" x2="56" y2="40" stroke="#9ca3af" stroke-width="2"/>
          </svg>

          <div class="cargo-stats">
            <div class="cargo-stat">
              <span class="cargo-num">{{ displayStats.totalPieces }}</span>
              <span class="cargo-unit">总张数</span>
            </div>
            <div class="cargo-stat">
              <span class="cargo-num">{{ displayStats.totalPackages }}</span>
              <span class="cargo-unit">包数</span>
            </div>
            <div class="cargo-stat">
              <span class="cargo-num">{{ displayStats.totalExtra }}</span>
              <span class="cargo-unit">零散</span>
            </div>
          </div>

          <button class="switch-btn" @click="goChangeTruck">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="1 4 1 10 7 10"/>
              <polyline points="23 20 23 14 17 14"/>
              <path d="M20.49 9A9 9 0 0 0 5.64 5.64L1 10m22 4l-4.64 4.36A9 9 0 0 1 3.51 15"/>
            </svg>
            更换货车
          </button>
        </div>

        <!-- 中间分割线 -->
        <div class="panel-divider"></div>

        <!-- 右侧：送货地址列表（可滚动） -->
        <div class="address-list">
          <div class="address-title">送货地址</div>
          <div class="address-items">
            <div
              v-for="(addr, idx) in displayedAddresses"
              :key="idx"
              class="address-row"
              :class="{ highlight: addr.isHighlighted }"
              :style="{
                borderLeft: `3px solid ${addr.color}`,
                background: addr.isHighlighted ? `${addr.color}18` : '#f9fafb'
              }"
            >
              <span class="addr-text" :title="addr.address">{{ addr.address || '—' }}</span>
              <span class="addr-time">{{ addr.time }}</span>
            </div>
            <div v-if="displayedAddresses.length === 0" class="empty-small">暂无送货地址</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getOrderList } from '@/api/order'
import { getTransportList } from '@/api/transport'

const router = useRouter()
const route = useRoute()

const orders = ref([])
const transports = ref([])
const currentYear = ref(new Date().getFullYear())
const currentMonth = ref(new Date().getMonth())
const selectedOrderId = ref(null)

async function fetchData() {
  const [orderRes, transportRes] = await Promise.all([
    getOrderList(),
    getTransportList()
  ])
  orders.value = orderRes.data
  transports.value = transportRes.data
}

onMounted(fetchData)

// 每次路由切回首页时重新拉取数据，确保与其他页新增/修改保持同步
watch(() => route.path, (to) => {
  if (to === '/') fetchData()
})

// ========== 辅助函数 ==========
const DELIVERY_COLORS = [
  '#6366f1', '#8b5cf6', '#ec4899', '#f97316', '#eab308',
  '#22c55e', '#14b8a6', '#0ea5e9', '#3b82f6', '#ef4444',
  '#a855f7', '#06b6d4', '#84cc16', '#f59e0b', '#64748b',
]

function getDeliveryColor(address, time) {
  const key = `${address || ''}|${time || ''}`
  let hash = 0
  for (let i = 0; i < key.length; i++) {
    hash = ((hash << 5) - hash) + key.charCodeAt(i)
    hash |= 0
  }
  return DELIVERY_COLORS[Math.abs(hash) % DELIVERY_COLORS.length]
}

function statusColor(status) {
  const map = {
    '待发货': '#f59e0b',
    '运输中': '#3b82f6',
    '已发货': '#8b5cf6',
    '已完成': '#22c55e',
    '已取消': '#9ca3af'
  }
  return map[status] || '#6b7280'
}

function formatDate(timestamp) {
  if (!timestamp) return '—'
  const d = new Date(timestamp)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

// ========== 订单筛选 ==========
const activeOrders = computed(() =>
  orders.value.filter(o => o.status !== '已完成' && o.status !== '已取消')
)

const selectedOrder = computed(() => {
  if (selectedOrderId.value == null) return null
  return orders.value.find(o => o.id === selectedOrderId.value) || null
})

function selectOrder(order) {
  selectedOrderId.value = selectedOrderId.value === order.id ? null : order.id
}

// ========== 日历逻辑 ==========
const dayHeaders = ['日', '一', '二', '三', '四', '五', '六']

const deliveriesByDate = computed(() => {
  const map = {}
  for (const order of orders.value) {
    if (!order.deliveryTime) continue
    const d = new Date(order.deliveryTime)
    const key = `${d.getFullYear()}-${d.getMonth()}-${d.getDate()}`
    if (!map[key]) map[key] = []
    map[key].push({
      color: getDeliveryColor(order.deliveryAddress, order.deliveryTime),
      customerName: order.customerName
    })
  }
  return map
})

const calendarDays = computed(() => {
  const today = new Date()
  const todayKey = `${today.getFullYear()}-${today.getMonth()}-${today.getDate()}`
  const year = currentYear.value
  const month = currentMonth.value

  const firstDay = new Date(year, month, 1)
  const daysInMonth = new Date(year, month + 1, 0).getDate()
  const startDayOfWeek = firstDay.getDay()

  const days = []

  const prevLastDay = new Date(year, month, 0).getDate()
  for (let i = startDayOfWeek - 1; i >= 0; i--) {
    const d = new Date(year, month - 1, prevLastDay - i)
    const key = `${d.getFullYear()}-${d.getMonth()}-${d.getDate()}`
    const delivs = deliveriesByDate.value[key] || []
    days.push({
      day: prevLastDay - i,
      isCurrentMonth: false,
      isToday: key === todayKey,
      hasDelivery: delivs.length > 0,
      deliveries: delivs,
      deliveryColor: delivs.length > 0 ? delivs[0].color : null
    })
  }

  for (let i = 1; i <= daysInMonth; i++) {
    const d = new Date(year, month, i)
    const key = `${d.getFullYear()}-${d.getMonth()}-${d.getDate()}`
    const delivs = deliveriesByDate.value[key] || []
    days.push({
      day: i,
      isCurrentMonth: true,
      isToday: key === todayKey,
      hasDelivery: delivs.length > 0,
      deliveries: delivs,
      deliveryColor: delivs.length > 0 ? delivs[0].color : null
    })
  }

  const remaining = 42 - days.length
  for (let i = 1; i <= remaining; i++) {
    const d = new Date(year, month + 1, i)
    const key = `${d.getFullYear()}-${d.getMonth()}-${d.getDate()}`
    const delivs = deliveriesByDate.value[key] || []
    days.push({
      day: i,
      isCurrentMonth: false,
      isToday: key === todayKey,
      hasDelivery: delivs.length > 0,
      deliveries: delivs,
      deliveryColor: delivs.length > 0 ? delivs[0].color : null
    })
  }

  return days
})

function prevMonth() {
  if (currentMonth.value === 0) {
    currentMonth.value = 11
    currentYear.value--
  } else {
    currentMonth.value--
  }
}

function nextMonth() {
  if (currentMonth.value === 11) {
    currentMonth.value = 0
    currentYear.value++
  } else {
    currentMonth.value++
  }
}

// ========== 运输统计（选中订单时显示订单数据，否则汇总全部进行中运输） ==========
const activeTransports = computed(() =>
  transports.value.filter(t => t.status !== '已完成' && t.status !== '已取消')
)

const displayStats = computed(() => {
  if (selectedOrder.value) {
    const o = selectedOrder.value
    return {
      totalPieces: o.tileQuantity || 0,
      totalPackages: o.packageCount || 0,
      totalExtra: o.extraPieces || 0
    }
  }
  let totalPieces = 0
  let totalPackages = 0
  let totalExtra = 0
  for (const t of activeTransports.value) {
    totalPieces += t.totalPieces || 0
    totalPackages += t.packageCount || 0
    totalExtra += t.extraPieces || 0
  }
  return { totalPieces, totalPackages, totalExtra }
})

// ========== 送货地址列表 ==========
const uniqueAddresses = computed(() => {
  const seen = new Set()
  const result = []
  for (const t of transports.value) {
    const key = `${t.deliveryAddress || ''}|${t.deliveryTime || ''}`
    if (!seen.has(key)) {
      seen.add(key)
      result.push({
        address: t.deliveryAddress,
        time: formatDate(t.deliveryTime),
        color: getDeliveryColor(t.deliveryAddress, t.deliveryTime)
      })
    }
  }
  return result
})

const displayedAddresses = computed(() => {
  if (!selectedOrder.value) {
    return uniqueAddresses.value.map(a => ({ ...a, isHighlighted: false }))
  }
  const sel = selectedOrder.value
  const selColor = getDeliveryColor(sel.deliveryAddress, sel.deliveryTime)
  const selTime = formatDate(sel.deliveryTime)

  return uniqueAddresses.value.map(a => ({
    ...a,
    isHighlighted: a.color === selColor && a.time === selTime
  }))
})

// ========== 页面跳转 ==========
function goOrderPage() {
  router.push('/order')
}

function goTransportPage() {
  router.push('/transport')
}

function goChangeTruck() {
  router.push({ path: '/truck', query: { selectMode: 'true', from: '/' } })
}
</script>

<style scoped>
.home-page {
  max-width: 1060px;
  width: 100%;
  margin: 0 auto;
  padding: 20px 20px 40px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

h1 {
  color: #333;
  margin: 0;
}

.sub-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.sub-title {
  font-size: 14px;
  color: #888;
  margin: 0;
}

/* ====== 可点击标题链接 ====== */
.link-title {
  cursor: pointer;
  transition: color 0.2s;
  user-select: none;
}

.link-title:hover {
  color: #3b82f6;
}

/* ====== 上半部分 ====== */
.top-row {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

/* ====== 左侧：订单卡片（可滚动） ====== */
.order-section {
  flex: 1;
  min-width: 0;
}

.section-header-bar {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 10px;
}

.section-header-bar h2 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.section-count {
  font-size: 12px;
  color: #888;
  background: #e5e7eb;
  padding: 2px 10px;
  border-radius: 20px;
}

.order-cards {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 380px;
  overflow-y: auto;
  padding-right: 4px;
}

.order-cards::-webkit-scrollbar {
  width: 5px;
}

.order-cards::-webkit-scrollbar-track {
  background: transparent;
}

.order-cards::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 10px;
}

.order-cards::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

.order-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 8px 12px;
  background: #fff;
  cursor: pointer;
  transition: all 0.15s;
  user-select: none;
  flex-shrink: 0;
}

.order-card:hover {
  box-shadow: 0 1px 6px rgba(0,0,0,0.08);
  border-color: #d1d5db;
}

.order-card.selected {
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
}

.card-top {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 5px;
}

.status-badge {
  font-size: 10px;
  color: #fff;
  padding: 1px 7px;
  border-radius: 8px;
  font-weight: 500;
  flex-shrink: 0;
}

.customer-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.amount {
  font-size: 14px;
  font-weight: 700;
  color: #e74c3c;
  margin-left: auto;
  flex-shrink: 0;
}

.card-info-row {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.info-chip {
  font-size: 11px;
  color: #666;
  background: #f3f4f6;
  padding: 2px 8px;
  border-radius: 4px;
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.info-chip.addr {
  max-width: 180px;
}

/* ====== 右侧：日历 ====== */
.calendar-section {
  width: 330px;
  flex-shrink: 0;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #fff;
  padding: 14px 16px;
}

.calendar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.month-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.month-btn {
  width: 28px;
  height: 28px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  background: #fff;
  cursor: pointer;
  font-size: 11px;
  color: #666;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.month-btn:hover {
  border-color: #3b82f6;
  color: #3b82f6;
  background: #f0f7ff;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 3px;
}

.day-header {
  text-align: center;
  font-size: 11px;
  color: #999;
  font-weight: 600;
  padding: 3px 0;
}

.day-cell {
  aspect-ratio: 1 / 1;
  border: 1px solid transparent;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  padding: 2px 1px;
  cursor: default;
  transition: background 0.15s;
}

.day-cell.other-month .day-num {
  color: #d0d0d0;
}

.day-cell.today {
  border-color: #3b82f6 !important;
  background: #eff6ff !important;
}

.day-cell.today .day-num {
  color: #1d4ed8;
  font-weight: 700;
}

.day-num {
  font-size: 12px;
  line-height: 1;
  margin-top: 1px;
}

.delivery-dots {
  display: flex;
  gap: 2px;
  margin-top: 2px;
}

.dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  flex-shrink: 0;
}

/* ====== 下半部分：运输货车面板 ====== */
.transport-section {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #fff;
  overflow: hidden;
}

.transport-section .section-header-bar {
  padding: 14px 20px 10px;
  margin-bottom: 0;
}

.transport-panel {
  display: flex;
  align-items: stretch;
  min-height: 200px;
}

/* 左侧：货车图标 + 统计 */
.truck-display {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 16px 20px;
  background: #fafafa;
}

.truck-icon {
  width: 64px;
  height: 64px;
}

.cargo-stats {
  display: flex;
  gap: 10px;
}

.cargo-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px 16px;
  min-width: 72px;
}

.cargo-num {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  line-height: 1.2;
}

.cargo-unit {
  font-size: 11px;
  color: #999;
  margin-top: 2px;
}

.switch-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 7px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: #fff;
  color: #555;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.switch-btn:hover {
  border-color: #3b82f6;
  color: #3b82f6;
  background: #f0f7ff;
}

/* 分割线 */
.panel-divider {
  width: 1px;
  background: #e5e7eb;
  align-self: stretch;
  margin: 16px 0;
}

/* 右侧：送货地址列表（可滚动） */
.address-list {
  flex: 1;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.address-title {
  font-size: 13px;
  font-weight: 600;
  color: #555;
  margin-bottom: 10px;
  flex-shrink: 0;
}

.address-items {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
  max-height: 250px;
  overflow-y: auto;
  padding-right: 4px;
}

.address-items::-webkit-scrollbar {
  width: 5px;
}

.address-items::-webkit-scrollbar-track {
  background: transparent;
}

.address-items::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 10px;
}

.address-items::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

.address-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 10px;
  background: #f9fafb;
  border-radius: 6px;
  gap: 10px;
  transition: all 0.2s;
  flex-shrink: 0;
}

.address-row.highlight {
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
  font-weight: 600;
}

.address-row.highlight .addr-text {
  color: #1f2937;
  font-weight: 600;
}

.address-row.highlight .addr-time {
  color: #666;
  font-weight: 600;
}

.addr-text {
  font-size: 13px;
  color: #555;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.addr-time {
  font-size: 12px;
  color: #999;
  flex-shrink: 0;
}

/* ====== 空状态 ====== */
.empty {
  text-align: center;
  color: #bbb;
  font-size: 13px;
  padding: 24px 0;
}

.empty-small {
  text-align: center;
  color: #ccc;
  font-size: 12px;
  padding: 20px 0;
}
</style>
