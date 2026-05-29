<template>
  <div class="order-page">
    <h1>订单表</h1>
    <div class="sub-row">
      <span class="sub-title">展示订单信息</span>
      <div class="toolbar">
        <button v-if="!isDeleteMode" class="btn-delete-mode" @click="isDeleteMode = true">
          批量删除
        </button>
        <div v-else class="delete-group">
          <button class="btn-select-all" @click="selectAll">全选</button>
          <button class="btn-delete" @click="handleBatchDelete">删除选中</button>
          <button class="btn-cancel" @click="cancelDeleteMode">取消</button>
        </div>
        <button class="add-btn" @click="openAdd">+ 新增订单</button>
      </div>
    </div>

    <!-- 进行中的订单 -->
    <div class="section">
      <div class="section-header" @click="showActive = !showActive">
        <div class="section-header-left">
          <h3>进行中</h3>
          <span class="count">{{ activeOrders.length }} 单</span>
        </div>
        <span class="arrow">{{ showActive ? '▲' : '▼' }}</span>
      </div>

      <div v-if="showActive" class="card-list">
        <div class="order-card" v-for="order in activeOrders" :key="order.id"
             :style="{
               borderLeft: `6px solid ${getDeliveryColor(order.deliveryAddress, order.deliveryTime)}`,
               background: `linear-gradient(to right, ${getDeliveryColor(order.deliveryAddress, order.deliveryTime)}08, transparent 60px)`
             }">
          <div class="card-top">
            <div class="card-top-left">
              <input
                v-if="isDeleteMode"
                type="checkbox"
                :value="order.id"
                v-model="selectedIds"
                @click.stop
              />
              <!-- 订单编号已隐藏 -->
            </div>
            <span class="status-badge" :style="{ background: statusColor(order.status) }">
              {{ order.status }}
            </span>
            <span class="amount">￥{{ order.totalAmount }}</span>
          </div>

          <div class="card-info-grid">
            <div class="info-item"><span class="lbl">客户</span><span class="val">{{ order.customerName }}</span></div>
            <div class="info-item"><span class="lbl">地址</span><span class="val">{{ order.deliveryAddress || '—' }}</span></div>
            <div class="info-item"><span class="lbl">瓦片</span><span class="val">{{ order.tileName || '—' }}</span></div>
            <div class="info-item"><span class="lbl">数量</span><span class="val">{{ order.tileQuantity ?? '—' }} 张</span></div>
            <div class="info-item" v-if="order.deliveryTime"><span class="lbl">配送</span><span class="val">{{ formatDate(order.deliveryTime) }}</span></div>
            <div class="info-item" v-if="order.remark"><span class="lbl">备注</span><span class="val">{{ order.remark }}</span></div>
          </div>

          <div v-if="!isDeleteMode" class="card-actions">
            <button class="action-btn edit-action" @click="openEdit(order)">编辑</button>
            <button
              v-if="order.status === '待发货'"
              class="action-btn ship-action"
              @click="quickShip(order)"
            >发货</button>
            <button
              v-if="order.status === '已发货'"
              class="action-btn complete-action"
              @click="quickComplete(order)"
            >完成</button>
            <button
              v-if="order.status !== '已完成' && order.status !== '已取消'"
              class="action-btn cancel-action"
              @click="quickCancel(order)"
            >取消</button>
          </div>
        </div>

        <div v-if="activeOrders.length === 0" class="empty">暂无进行中的订单</div>
      </div>
    </div>

    <!-- 已完成 / 已取消的订单 -->
    <div class="section">
      <div class="section-header finished-header" @click="showCompleted = !showCompleted">
        <div class="section-header-left">
          <h3>已完成 / 已取消</h3>
          <span class="count">{{ completedOrders.length }} 单</span>
        </div>
        <span class="arrow">{{ showCompleted ? '▲' : '▼' }}</span>
      </div>

      <div v-if="showCompleted" class="card-list">
        <div class="order-card" v-for="order in completedOrders" :key="order.id"
             :style="{
               borderLeft: `6px solid ${getDeliveryColor(order.deliveryAddress, order.deliveryTime)}`,
               background: `linear-gradient(to right, ${getDeliveryColor(order.deliveryAddress, order.deliveryTime)}08, transparent 60px)`
             }">
          <div class="card-top">
            <div class="card-top-left">
              <input
                v-if="isDeleteMode"
                type="checkbox"
                :value="order.id"
                v-model="selectedIds"
                @click.stop
              />
              <!-- 订单编号已隐藏 -->
            </div>
            <span class="status-badge" :style="{ background: statusColor(order.status) }">
              {{ order.status }}
            </span>
            <span class="amount">￥{{ order.totalAmount }}</span>
          </div>

          <div class="card-info-grid">
            <div class="info-item"><span class="lbl">客户</span><span class="val">{{ order.customerName }}</span></div>
            <div class="info-item"><span class="lbl">地址</span><span class="val">{{ order.deliveryAddress || '—' }}</span></div>
            <div class="info-item"><span class="lbl">瓦片</span><span class="val">{{ order.tileName || '—' }}</span></div>
            <div class="info-item"><span class="lbl">数量</span><span class="val">{{ order.tileQuantity ?? '—' }} 张</span></div>
            <div class="info-item" v-if="order.deliveryTime"><span class="lbl">配送</span><span class="val">{{ formatDate(order.deliveryTime) }}</span></div>
            <div class="info-item" v-if="order.remark"><span class="lbl">备注</span><span class="val">{{ order.remark }}</span></div>
          </div>

          <div v-if="!isDeleteMode" class="card-actions">
            <button class="action-btn edit-action" @click="openEdit(order)">编辑</button>
          </div>
        </div>

        <div v-if="completedOrders.length === 0" class="empty">暂无已完成或已取消的订单</div>
      </div>
    </div>

    <!-- 新增弹窗 -->
    <div v-if="showAdd" class="mask" @click="showAdd = false">
      <div class="modal" @click.stop>
        <h2 class="modal-title">新增订单</h2>
        <div class="form-grid">
          <div class="form-field">
            <label>客户姓名</label>
            <input v-model="addForm.customerName" placeholder="客户姓名" />
          </div>
          <div class="form-field">
            <label>总金额 (￥)</label>
            <input v-model.number="addForm.totalAmount" type="number" placeholder="0.00" step="0.01" />
          </div>
          <div class="form-field">
            <label>状态</label>
            <select v-model="addForm.status">
              <option value="待发货">待发货</option>
              <option value="运输中">运输中</option>
              <option value="已发货">已发货</option>
              <option value="已完成">已完成</option>
              <option value="已取消">已取消</option>
            </select>
          </div>
          <div class="form-field">
            <label>配送地址</label>
            <input v-model="addForm.deliveryAddress" placeholder="详细地址" />
          </div>
          <div class="form-field">
            <label>配送时间</label>
            <input v-model="addForm.deliveryTime" type="datetime-local" />
          </div>
          <div class="form-field">
            <label>瓦片名称</label>
            <select v-model="addForm.tileName" @change="calcPackage(addForm)">
              <option value="">请选择瓦片</option>
              <option v-for="t in tiles" :key="t.id" :value="t.tileName">{{ t.tileName }}（{{ t.unitQuantity }}张/包）</option>
            </select>
          </div>
          <div class="form-field">
            <label>总张数</label>
            <input v-model.number="addForm.tileQuantity" type="number" placeholder="0" @input="calcPackage(addForm)" />
          </div>
          <div class="form-field">
            <label>整包数量</label>
            <input v-model.number="addForm.packageCount" type="number" placeholder="自动计算" readonly />
          </div>
          <div class="form-field">
            <label>零散张数</label>
            <input v-model.number="addForm.extraPieces" type="number" placeholder="自动计算" readonly />
          </div>
          <div class="form-field full-width">
            <label>备注</label>
            <input v-model="addForm.remark" placeholder="备注信息（可选）" />
          </div>
        </div>
        <div class="modal-btns">
          <button class="btn-cancel-modal" @click="showAdd = false">取消</button>
          <button class="btn-submit" @click="submitAdd">确认新增</button>
        </div>
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <div v-if="showEdit" class="mask" @click="showEdit = false">
      <div class="modal" @click.stop>
        <h2 class="modal-title">编辑订单</h2>
        <div class="form-grid">
          <div class="form-field">
            <label>客户姓名</label>
            <input v-model="editForm.customerName" placeholder="客户姓名" />
          </div>
          <div class="form-field">
            <label>总金额 (￥)</label>
            <input v-model.number="editForm.totalAmount" type="number" placeholder="0.00" step="0.01" />
          </div>
          <div class="form-field">
            <label>状态</label>
            <select v-model="editForm.status">
              <option value="待发货">待发货</option>
              <option value="运输中">运输中</option>
              <option value="已发货">已发货</option>
              <option value="已完成">已完成</option>
              <option value="已取消">已取消</option>
            </select>
          </div>
          <div class="form-field">
            <label>配送地址</label>
            <input v-model="editForm.deliveryAddress" placeholder="详细地址" />
          </div>
          <div class="form-field">
            <label>配送时间</label>
            <input v-model="editForm.deliveryTime" type="datetime-local" />
          </div>
          <div class="form-field">
            <label>瓦片名称</label>
            <select v-model="editForm.tileName" @change="calcPackage(editForm)">
              <option value="">请选择瓦片</option>
              <option v-for="t in tiles" :key="t.id" :value="t.tileName">{{ t.tileName }}（{{ t.unitQuantity }}张/包）</option>
            </select>
          </div>
          <div class="form-field">
            <label>总张数</label>
            <input v-model.number="editForm.tileQuantity" type="number" placeholder="0" @input="calcPackage(editForm)" />
          </div>
          <div class="form-field">
            <label>整包数量</label>
            <input v-model.number="editForm.packageCount" type="number" placeholder="自动计算" readonly />
          </div>
          <div class="form-field">
            <label>零散张数</label>
            <input v-model.number="editForm.extraPieces" type="number" placeholder="自动计算" readonly />
          </div>
          <div class="form-field full-width">
            <label>备注</label>
            <input v-model="editForm.remark" placeholder="备注信息（可选）" />
          </div>
        </div>
        <div class="modal-btns">
          <button class="btn-cancel-modal" @click="showEdit = false">取消</button>
          <button class="btn-submit" @click="submitEdit">保存修改</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue"
import {
  getOrderList,
  addOrder,
  updateOrder,
  updateOrderStatus,
  deleteOrders
} from "@/api/order"
import { getTileList } from "@/api/tile"

const orders = ref([])
const tiles = ref([])
const showActive = ref(true)
const showCompleted = ref(true)

const fetchOrders = async () => {
  const res = await getOrderList()
  orders.value = res.data
}

onMounted(async () => {
  await fetchOrders()
  const tileRes = await getTileList()
  tiles.value = tileRes.data
})

function getUnitQuantity(tileName) {
  if (!tileName) return null
  const tile = tiles.value.find(t => t.tileName === tileName)
  return tile ? tile.unitQuantity : null
}

function calcPackage(form) {
  const tileName = form.tileName
  const tileQuantity = form.tileQuantity
  if (!tileName || !tileQuantity || tileQuantity <= 0) {
    form.packageCount = null
    form.extraPieces = null
    return
  }
  const unitQty = getUnitQuantity(tileName)
  if (unitQty && unitQty > 0) {
    form.packageCount = Math.floor(tileQuantity / unitQty)
    form.extraPieces = tileQuantity % unitQty
  }
}

const activeOrders = computed(() =>
  orders.value.filter(o => o.status !== '已完成' && o.status !== '已取消')
)

const completedOrders = computed(() =>
  orders.value.filter(o => o.status === '已完成' || o.status === '已取消')
)

// 基于配送地址+配送时间的颜色映射，相同地址+时间得到相同颜色
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

function toDatetimeLocal(timestamp) {
  if (!timestamp) return ''
  const d = new Date(timestamp)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const h = String(d.getHours()).padStart(2, '0')
  const min = String(d.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${day}T${h}:${min}`
}

// ========== 新增 ==========
const showAdd = ref(false)
const addForm = ref({
  customerName: '',
  totalAmount: null,
  status: '待发货',
  deliveryAddress: '',
  deliveryTime: '',
  tileName: '',
  tileQuantity: null,
  packageCount: null,
  extraPieces: null,
  remark: ''
})

const openAdd = () => {
  addForm.value = {
    customerName: '',
    totalAmount: null,
    status: '待发货',
    deliveryAddress: '',
    deliveryTime: '',
    tileName: '',
    tileQuantity: null,
    packageCount: null,
    extraPieces: null,
    remark: ''
  }
  showAdd.value = true
}

// 根据客户名称生成稳定的 customerId
function generateCustomerId(name) {
  if (!name) return 1
  let hash = 0
  for (let i = 0; i < name.length; i++) {
    hash = ((hash << 5) - hash) + name.charCodeAt(i)
    hash |= 0
  }
  return Math.abs(hash % 90000) + 10000
}

// 自动生成订单编号
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

const submitAdd = async () => {
  const data = {
    customerName: addForm.value.customerName || '未命名客户',
    totalAmount: (addForm.value.totalAmount && addForm.value.totalAmount !== 0)
      ? addForm.value.totalAmount : 0,
    status: addForm.value.status || '待发货',
    deliveryAddress: addForm.value.deliveryAddress || '',
    deliveryTime: addForm.value.deliveryTime
      ? new Date(addForm.value.deliveryTime).toISOString()
      : null,
    tileName: addForm.value.tileName || '',
    tileQuantity: addForm.value.tileQuantity || 0,
    packageCount: addForm.value.packageCount || 0,
    extraPieces: addForm.value.extraPieces || 0,
    remark: addForm.value.remark || '',
    orderNo: generateOrderNo(),
    customerId: generateCustomerId(addForm.value.customerName),
    createdAt: new Date().toISOString()
  }
  await addOrder(data)
  showAdd.value = false
  await fetchOrders()
}

// ========== 编辑 ==========
const showEdit = ref(false)
const editForm = ref({})

const openEdit = (order) => {
  editForm.value = {
    id: order.id,
    orderNo: order.orderNo,
    customerId: order.customerId,
    customerName: order.customerName,
    totalAmount: order.totalAmount,
    status: order.status,
    deliveryAddress: order.deliveryAddress || '',
    deliveryTime: toDatetimeLocal(order.deliveryTime),
    tileName: order.tileName || '',
    tileQuantity: order.tileQuantity ?? null,
    packageCount: order.packageCount ?? null,
    extraPieces: order.extraPieces ?? null,
    remark: order.remark || '',
    createdAt: order.createdAt
  }
  calcPackage(editForm.value)
  showEdit.value = true
}

const submitEdit = async () => {
  const data = {
    ...editForm.value,
    customerName: editForm.value.customerName || '未命名客户',
    totalAmount: (editForm.value.totalAmount && editForm.value.totalAmount !== 0)
      ? editForm.value.totalAmount : 0,
    deliveryTime: editForm.value.deliveryTime
      ? new Date(editForm.value.deliveryTime).toISOString()
      : null
  }
  await updateOrder(data)
  showEdit.value = false
  await fetchOrders()
}

// ========== 快捷状态变更 ==========
const quickShip = async (order) => {
  await updateOrderStatus({ id: order.id, status: '已发货' })
  await fetchOrders()
}

const quickComplete = async (order) => {
  await updateOrderStatus({ id: order.id, status: '已完成' })
  await fetchOrders()
}

const quickCancel = async (order) => {
  await updateOrderStatus({ id: order.id, status: '已取消' })
  await fetchOrders()
}

// ========== 批量删除 ==========
const isDeleteMode = ref(false)
const selectedIds = ref([])

const selectAll = () => {
  if (selectedIds.value.length === orders.value.length) {
    selectedIds.value = []
  } else {
    selectedIds.value = orders.value.map(o => o.id)
  }
}

const cancelDeleteMode = () => {
  isDeleteMode.value = false
  selectedIds.value = []
}

const handleBatchDelete = async () => {
  if (selectedIds.value.length === 0) {
    alert('请选择要删除的订单')
    return
  }
  await deleteOrders(selectedIds.value)
  await fetchOrders()
  cancelDeleteMode()
}
</script>

<style scoped>
.order-page {
  max-width: 950px;
  width: 100%;
  margin: 0 auto;
  padding: 20px 20px 40px;
}

.sub-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
}

.add-btn {
  padding: 6px 14px;
  background: #4ade80;
  border: none;
  color: white;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
}

.add-btn:hover {
  background: #22c55e;
}

.sub-title {
  font-size: 14px;
  color: #888;
  margin: 0;
}

h1 {
  color: #333;
}

/* ===== 分区 ===== */
.section {
  margin-top: 24px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  background: #f9fafb;
  cursor: pointer;
  user-select: none;
  transition: background 0.2s;
}

.section-header:hover {
  background: #f3f4f6;
}

.section-header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.count {
  font-size: 13px;
  color: #888;
  background: #e5e7eb;
  padding: 2px 10px;
  border-radius: 20px;
}

.arrow {
  font-size: 12px;
  color: #999;
}

/* ===== 卡片列表 ===== */
.card-list {
  padding: 12px 16px 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 520px;
  overflow-y: auto;
  padding-right: 10px;
}

.card-list::-webkit-scrollbar {
  width: 5px;
}

.card-list::-webkit-scrollbar-track {
  background: transparent;
}

.card-list::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 10px;
}

.card-list::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

/* ===== 订单卡片 ===== */
.order-card {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px 16px;
  background: #fff;
  transition: box-shadow 0.2s, border-color 0.2s;
}

.order-card:hover {
  border-color: #d1d5db;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-top {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}

.card-top-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.order-no {
  font-weight: 600;
  font-size: 15px;
  color: #1f2937;
}

.status-badge {
  font-size: 12px;
  color: #fff;
  padding: 3px 10px;
  border-radius: 12px;
  font-weight: 500;
}

.card-info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 3px 16px;
  margin-bottom: 8px;
}

.info-item {
  display: flex;
  gap: 4px;
  font-size: 13px;
  align-items: baseline;
  min-width: 0;
}

.info-item .lbl {
  color: #999;
  flex-shrink: 0;
}

.info-item .val {
  color: #555;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.amount {
  font-size: 16px;
  font-weight: 700;
  color: #e74c3c;
  margin-left: auto;
  flex-shrink: 0;
}

.remark {
  font-size: 12px;
  color: #aaa;
}

/* ===== 卡片操作按钮 ===== */
.card-actions {
  display: flex;
  gap: 6px;
  padding-top: 6px;
  border-top: 1px dashed #e5e7eb;
}

.action-btn {
  padding: 3px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  background: #fff;
  transition: 0.2s;
}

.edit-action:hover {
  border-color: #3b82f6;
  color: #3b82f6;
}

.ship-action:hover {
  border-color: #8b5cf6;
  color: #8b5cf6;
}

.complete-action:hover {
  border-color: #22c55e;
  color: #22c55e;
}

.cancel-action:hover {
  border-color: #9ca3af;
  color: #9ca3af;
}

/* ===== 空状态 ===== */
.empty {
  text-align: center;
  color: #bbb;
  font-size: 14px;
  padding: 32px 0;
}

/* ===== 删除模式 ===== */
.delete-group {
  display: flex;
  gap: 8px;
}

.btn-delete-mode {
  background: #e74c3c;
  color: white;
  border: none;
  padding: 6px 14px;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
}

.btn-select-all {
  background: #6aa9ff;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
}

.btn-delete {
  background: #e74c3c;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
}

.btn-cancel {
  background: #2ecc71;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
}

.btn-delete-mode:hover,
.btn-select-all:hover,
.btn-delete:hover,
.btn-cancel:hover {
  opacity: 0.85;
}

/* ===== 弹窗 ===== */
.mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  width: 520px;
  max-height: 80vh;
  background: white;
  border-radius: 12px;
  padding: 28px 32px;
  overflow-y: auto;
}

.modal-title {
  margin: 0 0 20px;
  font-size: 18px;
  color: #333;
  text-align: center;
}

.form-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
}

.form-field {
  width: calc(50% - 7px);
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.form-field.full-width {
  width: 100%;
}

.form-field label {
  font-size: 13px;
  color: #666;
}

.form-field input,
.form-field select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.form-field input:focus,
.form-field select:focus {
  border-color: #4ade80;
}

.form-field input[readonly] {
  background: #f5f5f5;
  color: #888;
  cursor: default;
}

.modal-btns {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.btn-cancel-modal {
  padding: 8px 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  font-size: 14px;
}

.btn-cancel-modal:hover {
  background: #f3f4f6;
}

.btn-submit {
  padding: 8px 20px;
  border: none;
  border-radius: 8px;
  background: #111;
  color: white;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-submit:hover {
  background: #22c55e;
}
</style>
