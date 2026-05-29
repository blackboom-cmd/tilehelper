<template>
  <div
      class="right-panel"
      :class="{ collapsed: collapsed }"
      :style="{ backgroundColor: lightThemeColor }"
  >

    <!-- ⭐内容区 -->
    <div class="body" v-if="!collapsed">

      <!-- ⭐ 上盒子：订单信息 -->
      <div class="top-box">
        <div class="order-section" v-if="selectedOrderTile">
          <div class="order-tile-name">{{ selectedOrderTile.tileName }}</div>
          <div class="order-form">
            <div class="order-field">
              <label>单价（元/张）</label>
              <input v-model.number="orderPrice" type="number" step="0.01" />
            </div>
            <div class="order-field">
              <label>客户信息</label>
              <input v-model="customerName" type="text" placeholder="客户姓名" />
            </div>
            <div class="order-field">
              <label>送货地点</label>
              <input v-model="deliveryAddress" type="text" placeholder="送货地点" />
            </div>
            <div class="order-field">
              <label>送货时间</label>
              <input v-model="deliveryTime" type="datetime-local" />
            </div>
          </div>
        </div>
        <div class="order-empty" v-else>
          <div class="empty-icon">📋</div>
          <div class="empty-text">点击左侧瓦片卡片<br/>快速生成订单</div>
        </div>
      </div>

      <!-- ⭐ 下盒子：数量 + 生成 + 计算器 -->
      <div class="bottom-box">

        <!-- 数量 + 总价 + 生成（数值来源：下方计算器） -->
        <div class="quantity-section" v-if="selectedOrderTile">
          <div class="order-summary">
            <div class="summary-item">
              <span>数量</span>
              <span class="summary-val">{{ quantityNum }} 张</span>
            </div>
            <div class="summary-item">
              <span>单价</span>
              <span class="summary-val">￥{{ unitPriceNum }}</span>
            </div>
            <div class="summary-item total">
              <span>订单总价</span>
              <span class="summary-val">￥{{ unitPriceNum * quantityNum }}</span>
            </div>
          </div>
          <button
            class="generate-btn"
            :disabled="quantityNum <= 0"
            @click="generateOrder"
          >
            生成订单
          </button>
        </div>

        <!-- 横向输入 -->
        <div class="info-box">

          <div
            class="info-item"
            :class="{ active: activeField === 'unitPrice' }"
            @click="activeField = 'unitPrice'"
          >
            <span>单价</span>
            <input v-model="unitPrice" />
          </div>

          <div
            class="info-item"
            :class="{ active: activeField === 'quantity' }"
            @click="activeField = 'quantity'"
          >
            <span>数量</span>
            <input v-model="quantity" />
          </div>

          <div class="info-item">
            <span>总价</span>
            <span class="result">{{ totalPrice }}</span>
          </div>

        </div>

        <!-- ⭐ 原来的全部内容（你计算器整体原封不动放进来） -->
        <div class="calc-panel">

          <!-- 显示屏 -->
          <div class="screen">
            <div class="screen-title">计算器</div>
            <div class="screen-value">{{ activeDisplay }}</div>
          </div>

          <!-- 键盘 -->
          <div class="keyboard">

            <div class="keypad">

              <button v-for="n in 9" :key="n" @click="appendNumber(n)">
                {{ n }}
              </button>

              <button class="dot" @click="appendDecimal">.</button>
              <button @click="appendNumber(0)">0</button>
              <button class="back" @click="backspace">⌫</button>
              <button class="clear" @click="clearAll">C</button>

            </div>

          </div>

        </div>

      </div>

    </div>

    <!-- ⭐收缩把手（必须在里面） -->
    <div
        class="toggle"
        @click="
    collapsed = !collapsed;
    panelWidth.value = collapsed ? 0 : 44
  "
    >
      {{ collapsed ? '展开' : '收起' }}
    </div>

  </div>
</template>

<script setup>

import { ref } from 'vue'
import { inject } from 'vue'

const collapsed = ref(false)
const panelWidth = inject('panelWidth')
const selectedOrderTile = inject('selectedOrderTile')

import { useTheme } from '../composables/useTheme'

const { themeColor } = useTheme()

import { computed, watch } from 'vue'

const lightThemeColor = computed(() => {
  const hex = themeColor.value.replace('#', '')

  const r = parseInt(hex.substr(0, 2), 16)
  const g = parseInt(hex.substr(2, 2), 16)
  const b = parseInt(hex.substr(4, 2), 16)

  const factor = 0.8 // ⭐变浅程度（0.7更浅，0.9更接近原色）

  const lr = Math.min(255, r + (255 - r) * factor)
  const lg = Math.min(255, g + (255 - g) * factor)
  const lb = Math.min(255, b + (255 - b) * factor)

  return `rgb(${lr}, ${lg}, ${lb})`
})

// ========== 订单生成相关 ==========
const orderPrice = ref(0)
const customerName = ref('')
const deliveryAddress = ref('')
const deliveryTime = ref('')
const orderGenerating = ref(false)

watch(selectedOrderTile, (tile) => {
  if (tile) {
    const price = tile.price ?? 0
    orderPrice.value = price
    unitPrice.value = String(price)
    customerName.value = ''
    deliveryAddress.value = ''
    deliveryTime.value = ''
    if (collapsed.value) {
      collapsed.value = false
      panelWidth.value = 44
    }
  }
})

import { addOrder } from '@/api/order'

function generateOrderNo() {
  const now = new Date()
  const y = now.getFullYear()
  const m = String(now.getMonth() + 1).padStart(2, '0')
  const d = String(now.getDate()).padStart(2, '0')
  const h = String(now.getHours()).padStart(2, '0')
  const min = String(now.getMinutes()).padStart(2, '0')
  const s = String(now.getSeconds()).padStart(2, '0')
  return `ORD-${y}${m}${d}${h}${min}${s}`
}

function generateCustomerId(name) {
  if (!name) return 1
  let hash = 0
  for (let i = 0; i < name.length; i++) {
    hash = ((hash << 5) - hash) + name.charCodeAt(i)
    hash |= 0
  }
  return Math.abs(hash % 90000) + 10000
}

const generateOrder = async () => {
  const qty = quantityNum.value
  if (!selectedOrderTile.value || qty <= 0) {
    return
  }
  orderGenerating.value = true
  try {
    const tile = selectedOrderTile.value
    const total = unitPriceNum.value * qty
    const data = {
      customerName: customerName.value || '未命名客户',
      totalAmount: total,
      status: '待发货',
      deliveryAddress: deliveryAddress.value || '',
      deliveryTime: deliveryTime.value
        ? new Date(deliveryTime.value).toISOString()
        : null,
      tileName: tile.tileName,
      tileQuantity: qty,
      packageCount: 0,
      extraPieces: 0,
      remark: '',
      orderNo: generateOrderNo(),
      customerId: generateCustomerId(customerName.value),
      createdAt: new Date().toISOString()
    }
    await addOrder(data)
    quantity.value = '0'
    alert('订单已生成，请在订单表和运输表中查看')
  } catch (e) {
    alert('订单生成失败，请重试')
  } finally {
    orderGenerating.value = false
  }
}

const unitPrice = ref('0')
const quantity = ref('0')

const unitPriceNum = computed(() => parseFloat(unitPrice.value) || 0)
const quantityNum = computed(() => parseInt(quantity.value) || 0)

const activeField = ref('quantity')

const totalPrice = computed(() => {
  return unitPriceNum.value * quantityNum.value
})

const activeDisplay = computed(() => {
  if (activeField.value === 'unitPrice') return unitPrice.value
  if (activeField.value === 'quantity') return quantity.value
  return totalPrice.value
})

function appendNumber(n) {
  if (activeField.value === 'unitPrice') {
    const cur = unitPrice.value === '0' ? '' : unitPrice.value
    unitPrice.value = cur + n
  } else {
    const cur = quantity.value === '0' ? '' : quantity.value
    quantity.value = cur + n
  }
}

function appendDecimal() {
  if (activeField.value === 'unitPrice') {
    if (!unitPrice.value.includes('.')) {
      unitPrice.value = unitPrice.value + '.'
    }
  }
  // 数量是整数，不需要小数点
}

function clearAll() {
  if (activeField.value === 'unitPrice') unitPrice.value = '0'
  else quantity.value = '0'
}

function backspace() {
  if (activeField.value === 'unitPrice') {
    unitPrice.value = unitPrice.value.slice(0, -1) || '0'
  } else {
    quantity.value = quantity.value.slice(0, -1) || '0'
  }
}

// ========== 单价双向同步：上方 orderPrice ↔ 下方计算器 unitPrice ==========
let priceSyncing = false
watch(orderPrice, (val) => {
  if (priceSyncing) return
  priceSyncing = true
  unitPrice.value = String(val)
  priceSyncing = false
})
watch(unitPrice, (val) => {
  if (priceSyncing) return
  priceSyncing = true
  orderPrice.value = parseFloat(val) || 0
  priceSyncing = false
})

import { onMounted } from 'vue'

onMounted(() => {
  collapsed.value = true   // ⭐默认收起
  panelWidth.value = 0     // ⭐同步外部宽度（很关键）
})

</script>

<style scoped>
.right-panel {
  position: relative;
  flex-shrink: 0;

  width: 320px;
  height: 100vh;

  transition: width 0.25s ease;

  overflow: visible;

  backdrop-filter: blur(10px);  /* ⭐玻璃感 */
}

/* 收起 */
.right-panel.collapsed {
  width: 0;
}

/* 内容 */
.body {
  width: 320px;
  height: 100%;

  display: flex;
  flex-direction: column;
}

/* ⭐关键按钮：永远贴着“面板左边缘” */
.toggle {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);

  width: 40px;
  height: 110px;

  background: rgba(0, 0, 0, 0.35);   /* ⭐半透明 */
  backdrop-filter: blur(8px);        /* ⭐玻璃感 */
  -webkit-backdrop-filter: blur(8px);

  color: #fff;
  font-size: 12px;
  font-weight: bold;

  display: flex;
  align-items: center;
  justify-content: center;

  cursor: pointer;

  border-radius: 12px 0 0 12px;     /* ⭐更圆润 */

  z-index: 9999;

  right: 100%;

  transition: all 0.2s ease;
}

.text {
  font-size: 12px;
  line-height: 14px;
  text-align: center;
  letter-spacing: 1px;  /* ⭐稍微拉开间距，更高级 */
}

.toggle:hover {
  background: rgba(0, 0, 0, 0.55);
  transform: translateY(-50%) scale(1.05);
}

/* ⭐ 关键：让两个盒子真正参与 3:7 */
.top-box {
  flex: 4;
  width: 100%;

  border-bottom: 1px solid rgba(0, 0, 0, 0.12); /* ⭐分割线 */
  box-sizing: border-box;
  border-bottom-left-radius: 8px;
  border-bottom-right-radius: 8px;

  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

/* ===== 订单生成区域 ===== */
.order-section {
  width: 92%;
  padding: 12px 14px;
}

.order-tile-name {
  font-size: 16px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 10px;
  color: #333;
  letter-spacing: 0.5px;
  padding: 6px;
  background: rgba(255,255,255,0.6);
  border-radius: 8px;
}

.order-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.order-field {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.order-field label {
  font-size: 12px;
  color: #666;
  flex-shrink: 0;
  white-space: nowrap;
}

.order-field input {
  width: 100px;
  height: 28px;
  font-size: 13px;
  text-align: center;
  border-radius: 6px;
  border: 1px solid rgba(0,0,0,0.2);
  outline: none;
  padding: 0 6px;
  box-sizing: border-box;
}

.order-field input:focus {
  border-color: #4ade80;
}

.order-field input::-webkit-outer-spin-button,
.order-field input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.order-field input[type=number] {
  -moz-appearance: textfield;
}

.order-total {
  font-size: 13px;
  text-align: center;
  color: #666;
  margin-top: 2px;
}

.total-amount {
  font-size: 16px;
  font-weight: bold;
  color: #e74c3c;
}

.generate-btn {
  width: 100%;
  height: 36px;
  margin-top: 4px;
  border: none;
  border-radius: 8px;
  background: #111;
  color: white;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
  transition: 0.2s;
}

.generate-btn:hover:not(:disabled) {
  background: #22c55e;
}

.generate-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

/* ===== 空状态提示 ===== */
.order-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #aaa;
}

.empty-icon {
  font-size: 28px;
  opacity: 0.6;
}

.empty-text {
  font-size: 12px;
  text-align: center;
  line-height: 1.6;
}

/* ===== 数量输入区域（下盒子顶部） ===== */
.quantity-section {
  width: 92%;
  padding: 8px 10px;
  margin-top: 8px;
  background: rgba(255,255,255,0.6);
  border-radius: 10px;

  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.quantity-row {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.quantity-row label {
  font-size: 12px;
  color: #666;
  flex-shrink: 0;
  white-space: nowrap;
}

.quantity-row input {
  width: 100px;
  height: 28px;
  font-size: 13px;
  text-align: center;
  border-radius: 6px;
  border: 1px solid rgba(0,0,0,0.2);
  outline: none;
  padding: 0 6px;
  box-sizing: border-box;
}

.quantity-row input:focus {
  border-color: #4ade80;
}

.quantity-row input::-webkit-outer-spin-button,
.quantity-row input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.quantity-row input[type=number] {
  -moz-appearance: textfield;
}

.bottom-box {
  flex: 6;
  width: 100%;
  display: flex;

  flex-direction: column;

  justify-content: flex-start;
  align-items: center;

  border-top: 1px solid rgba(0, 0, 0, 0.08); /* ⭐上边界 */
  box-sizing: border-box;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}

.calc-panel {
  width: 92%;
  padding: 14px;
  border-radius: 14px;
  background: rgba(255,255,255,0.75);

  flex: 0;              /* ⭐关键：取消撑满 */
  margin-top: 10px;      /* ⭐往下留一点空间 */
  margin-bottom: 8px;  /* ⭐底部呼吸空间 */

  display: flex;
  flex-direction: column;  /* ⭐内部变竖向结构 */

  box-shadow: 0 6px 18px rgba(0,0,0,0.08);
}

/* ⭐让 info-box 横排 */
.info-box {
  width: 92%;

  display: flex;
  flex-direction: row;           /* ⭐强制横排 */

  justify-content: space-between;
  align-items: center;

  margin-top: 6px;
  margin-bottom: 10px;

  padding: 8px 6px;
  background: rgba(255,255,255,0.7);
  border-radius: 8px;
}

.info-item {
  flex: 1;

  display: flex;
  flex-direction: column;   /* ⭐文字在上，输入在下 */
  align-items: center;

  font-size: 12px;

  padding: 4px 2px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.15s;
}

.info-item:hover {
  background: rgba(0,0,0,0.04);
}

.info-item.active {
  background: rgba(74, 222, 128, 0.18);
}

.info-item.active span {
  color: #333;
  font-weight: bold;
}

.info-item input {
  width: 60px;              /* ⭐关键：适配320侧栏 */
  height: 24px;

  font-size: 12px;
  text-align: center;

  border-radius: 6px;
  border: 1px solid rgba(0,0,0,0.2);

  outline: none;
  pointer-events: none;     /* 避免点击 input 时不触发父级切换 */
}

/* Chrome / Edge / Safari */
.info-item input::-webkit-outer-spin-button,
.info-item input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

/* Firefox */
.info-item input[type=number] {
  -moz-appearance: textfield;
}

/* 显示屏 */
.screen {
  background: linear-gradient(135deg, #1e1e2f, #2b2b3d);

  color: #00ffd5;   /* ⭐青绿色，比纯绿高级 */

  padding: 12px;
  border-radius: 10px;

  margin-bottom: 10px;
  font-family: monospace;

  box-shadow: inset 0 0 10px rgba(0,0,0,0.4);
}

.screen-title {
  font-size: 12px;
  opacity: 0.7;
  color: #cfcfcf;
}

.screen-value {
  font-size: 20px;
  text-align: right;
}

/* 横向计算 */
.calc-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.calc-box {
  flex: 1;
  text-align: center;

  display: flex;
  flex-direction: column;
  align-items: center;

  gap: 4px;
}

.calc-box input {
  width: 70px;        /* ⭐固定宽度（适配320侧栏） */
  height: 24px;       /* ⭐变矮 */
  padding: 2px 6px;

  font-size: 12px;
  text-align: center;

  border-radius: 6px;
  border: 1px solid rgba(0,0,0,0.2);

  outline: none;
}

/* Chrome / Edge / Safari */
.calc-box input::-webkit-outer-spin-button,
.calc-box input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

/* Firefox */
.calc-box input[type=number] {
  -moz-appearance: textfield;
}

.calc-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  gap: 6px; /* ⭐关键：避免挤 */
}

.result {
  font-size: 14px;
  font-weight: bold;
}

/* 键盘 */
.keyboard {
  margin-top: 10px;
}

.keypad {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;   /* ⭐加大间距 */

  margin-top: 10px;
}

.keypad button {
  padding: 14px 0;          /* ⭐变高 */
  font-size: 14px;          /* ⭐更清晰 */

  border: none;
  cursor: pointer;

  border-radius: 10px;      /* ⭐更圆 */
  background: #eee;

  transition: all 0.15s ease;
}

.keypad button:hover {
  background: #ddd;
  transform: scale(1.03);
}

.keypad .clear {
  grid-column: 1 / -1;
  background: #ffcccc;
}

.keypad .dot {
  background: #e0e0e0;
  font-weight: bold;
}

.keypad .back {
  background: #ccccff;
}

</style>